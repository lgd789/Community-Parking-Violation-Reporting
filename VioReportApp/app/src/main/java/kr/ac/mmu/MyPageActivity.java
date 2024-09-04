package kr.ac.mmu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyPageActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String id, carNum;
    private boolean isLoggedIn;
    private TextView textViewId, textViewCarNumber;
    private TextView textViewRank, textViewPoint, textViewReportCnt;
    private ImageButton imageButtonMyPageBack;
    private Button buttonUpdateInfo, buttonUpdateCarNum, buttonLogout, buttonSecession;

    List<Report> reports = new ArrayList<>();
    private RecyclerView recyclerViewMyReport;
    private ReportListHomeAdapter adapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        // 뒤로가기 버튼
        imageButtonMyPageBack = findViewById(R.id.imageButton_myPage_back);
        imageButtonMyPageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textViewId = findViewById(R.id.textView_id);
        textViewId.setText(id);

        textViewCarNumber = findViewById(R.id.textView_carNumber);
        textViewRank = findViewById(R.id.textView_rank);
        textViewPoint = findViewById(R.id.textView_point);
        textViewReportCnt = findViewById(R.id.textView_report_count);

        try {
            String messages = new HttpTask().execute("#myPage", id).get();

            String[] message = messages.split("/");
            textViewReportCnt.setText(message[0]);
            textViewRank.setText(message[1]);
            textViewPoint.setText(message[2]);

            if (message.length == 4) {
                carNum = message[3];
            }
            else {
                carNum = "";
            }
            textViewCarNumber.setText(carNum);

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        buttonUpdateInfo = findViewById(R.id.button_update_user_info);
        buttonUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserInfoSetActivity.class);
                intent.putExtra("id", id);

                startActivity(intent);
            }
        });

        buttonUpdateCarNum = findViewById(R.id.button_update_carnum);
        buttonUpdateCarNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserCarNumSetActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("carNum", carNum);

                startActivity(intent);
            }
        });

        buttonLogout = findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
                editor = sp.edit();
                id = sp.getString("userId", null);
                isLoggedIn = sp.getBoolean("isLoggedIn", false);
                
                if (isLoggedIn) {
                    editor.clear();
                    editor.apply();

                    isLoggedIn = false;
                    id = null;

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 액티비티 스택을 초기화합니다
                    startActivity(intent);
                    finish(); // 현재 액티비티를 종료합니다

                }
            }
        });

        buttonSecession = findViewById(R.id.button_mypage_secession);
        buttonSecession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getList(id);

        // recyclerView
        recyclerViewMyReport = findViewById(R.id.recyclerView_mypage_list);
        recyclerViewMyReport.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyReport.setHasFixedSize(true);
        adapterList = new ReportListHomeAdapter(reports);

        adapterList.setOnItemClickListener(new ReportListHomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(getApplicationContext(), ShowRInfoActivity.class);
                intent.putExtra("reportId", reports.get(pos).getReportNumber());
                intent.putExtra("reportCount", reports.get(pos).getReportCount());
                intent.putExtra("reportLatitude", reports.get(pos).getLatitude());
                intent.putExtra("reportLongitude", reports.get(pos).getLongitude());
                startActivity(intent);
            }
        });
        recyclerViewMyReport.setAdapter(adapterList);


    }

    private void getList(String id){
        reports.clear();
        try {
            String messages = new HttpTask().execute("#showList", "-1", id).get();

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
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_secession_dailog, findViewById(R.id.dialog_layout));

        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button_dailog_secession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpTask().execute("#secession", id);

                sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
                editor = sp.edit();
                id = sp.getString("userId", null);
                isLoggedIn = sp.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    editor.clear();
                    editor.apply();

                    isLoggedIn = false;
                    id = null;

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 액티비티 스택을 초기화합니다
                    startActivity(intent);
                    finish();

                }
            }
        });

        view.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

        WindowManager.LayoutParams params=alertDialog.getWindow().getAttributes();
        params.width=1000;
        params.height=500;
        alertDialog.getWindow().setAttributes(params);
    }
}