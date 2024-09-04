package kr.ac.mmu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ShowRListActivity extends AppCompatActivity {
    private ImageButton imageButtonRlistBack, imageButtonSearch;
    private EditText editTextSearch;
    private RecyclerView recyclerViewRlist;
    private ReportListAdapter adapterRlist;
    List<Report> reports = new ArrayList<>();
    private String searchKeyword = ""; // 검색어 저장 변수
    private List<Report> filteredReports = new ArrayList<>(); // 필터링된 결과 저장 변수
    private Spinner spinnerSort;
    private LocationHelper locationHelper;
    private double currentLatitude;
    private double currentLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_rlist);

        locationHelper = new LocationHelper(this);
        // 뒤로가기 버튼
        imageButtonRlistBack = findViewById(R.id.imageButton_rlist_back);
        imageButtonRlistBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        recyclerViewRlist = findViewById(R.id.recyclerView_Rlist);
        recyclerViewRlist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRlist.setHasFixedSize(true);

        editTextSearch = findViewById(R.id.editText_search);
        editTextSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    imageButtonSearch.performClick();
                    return true;
                }
                return false;
            }
        });
        imageButtonSearch = findViewById(R.id.imageButton_search);
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKeyword = editTextSearch.getText().toString().trim();
                filterAndSortReports(searchKeyword, spinnerSort.getSelectedItemPosition());
            }
        });

        spinnerSort = findViewById(R.id.spinner_sort);
        ArrayAdapter sortAdapter = ArrayAdapter.createFromResource(this, R.array.sort, android.R.layout.simple_spinner_dropdown_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSortReports(searchKeyword, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        currentLatitude = locationHelper.getLatitude();
        currentLongitude = locationHelper.getLongitude();

        int scrollCount = 0;
        httptask(scrollCount);

        adapterRlist = new ReportListAdapter(filteredReports);
        adapterRlist.setOnItemClickListener(new ReportListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(getApplicationContext(), ShowRInfoActivity.class);
                intent.putExtra("reportId", filteredReports.get(pos).getReportNumber());
                intent.putExtra("reportCount", filteredReports.get(pos).getReportCount());
                intent.putExtra("reportLatitude", filteredReports.get(pos).getLatitude());
                intent.putExtra("reportLongitude", filteredReports.get(pos).getLongitude());
                startActivity(intent);
            }
        });
        recyclerViewRlist.setAdapter(adapterRlist);
    }

    private void filterAndSortReports(String searchKeyword, int sortPosition) {
        this.searchKeyword = searchKeyword;
        filteredReports.clear();

        for (Report report : reports) {
            if (report.getAddress().toLowerCase().contains(searchKeyword.toLowerCase())
                    || report.getLicensePlateNumber().toLowerCase().contains(searchKeyword.toLowerCase())
                    || report.getReportNumber().toLowerCase().contains(searchKeyword.toLowerCase())) {
                filteredReports.add(report);
            }
        }

        switch (sortPosition) {
            case 0:
                // 기본 정렬 (reports 리스트 그대로 유지)
                break;
            case 1:
                // reports의 역순으로 정렬
                Collections.reverse(filteredReports);
                break;
            case 2:
                // 내 위치에서 가까운 순서로 정렬
                Collections.sort(filteredReports, new Comparator<Report>() {
                    @Override
                    public int compare(Report r1, Report r2) {
                        double distance1 = calculateDistance(currentLatitude, currentLongitude, Double.parseDouble(r1.getLatitude()), Double.parseDouble(r1.getLongitude()));
                        double distance2 = calculateDistance(currentLatitude, currentLongitude, Double.parseDouble(r2.getLatitude()), Double.parseDouble(r2.getLongitude()));
                        return Double.compare(distance1, distance2);
                    }
                });
                break;
            default:
                break;
        }

        adapterRlist.setReports(filteredReports);
        adapterRlist.notifyDataSetChanged();
    }

    private void httptask(int scrollCount) {
        reports.clear();
        try {
            String messages = new HttpTask().execute("#showList", Integer.toString(scrollCount), "-1").get();

            if (!messages.equals("false")) {
                String[] message = messages.split("/");

                int i = 0;
                while (true) {
                    String reportNumber = message[i++];
                    String address = message[i++];
                    String licensePlateNumber = message[i++];
                    String reportCount = message[i++];
                    String latitude = message[i++];
                    String longitude = message[i++];

                    Report report = new Report(reportNumber, address, licensePlateNumber, reportCount, latitude, longitude);
                    reports.add(report);
                    if (i >= message.length - 1) break;
                }
                scrollCount++;
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 두 지점 간의 거리를 계산하는 메서드 (Haversine 공식 사용)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // 지구의 반경 (단위: km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }
}
