package kr.ac.mmu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {
    private Button buttonHomeReport;
    private ImageButton imageButtonMap, imageButtonHomeReport, imageButtonMyPage;
    private ImageButton imageButtonHomeRank, imageButtonHomeList, imageButtonLogout;
    private ImageButton plusButtonHomeList, plusButtonHomeRank;
    private TextView textUserId;
    private RecyclerView recyclerViewHomeRank, recyclerViewHomeList;
    private RankListAdapter adapterRank;
    private ReportListHomeAdapter adapterList;
    List<Rank> ranks = new ArrayList<>();
    List<Report> reports = new ArrayList<>();
    private String imagePath = "";
    AnimationDrawable ani;
    private Uri image_uri = null;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String id;
    private boolean isLoggedIn;

    private static final int REQUEST_PERMISSIONS_CODE = 1000;
    private static final int REQUEST_BACKGROUND_PERMISSIONS_CODE = 2000;
    private static final int REQUEST_POST_PERMISSIONS_CODE = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getColor();

        sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
        editor = sp.edit();
        id = sp.getString("userId", null);
        isLoggedIn = sp.getBoolean("isLoggedIn", false);

        imageButtonMap = (ImageButton) findViewById(R.id.imageButton_home_map);
        imageButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("mapBtn", "start");
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                Log.e("mapBtn", "end");
            }
        });

        buttonHomeReport = (Button) findViewById(R.id.button_home_report);
        buttonHomeReport.setOnClickListener(new BtnOnClickListener());
        imageButtonHomeReport = findViewById(R.id.imageButton_home_report);
        imageButtonHomeReport.setOnClickListener(new BtnOnClickListener());

        plusButtonHomeList = findViewById(R.id.plusButton_home_list);
        plusButtonHomeList.setOnClickListener(new BtnOnClickListener());
        imageButtonHomeList = findViewById(R.id.imageButton_home_list);
        imageButtonHomeList.setOnClickListener(new BtnOnClickListener());

        imageButtonMyPage = findViewById(R.id.imageButton_mypage);
        imageButtonMyPage.setOnClickListener(new BtnOnClickListener());

        imageButtonLogout = findViewById(R.id.imageButton_logout);
        imageButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn) {
                    editor.clear();
                    editor.apply();

                    isLoggedIn = false;
                    id = null;

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                }
            }
        });

        plusButtonHomeRank = findViewById(R.id.plusButton_home_rank);
        plusButtonHomeRank.setOnClickListener(new BtnOnClickListener());
        imageButtonHomeRank = findViewById(R.id.imageButton_home_rank);
        imageButtonHomeRank.setOnClickListener(new BtnOnClickListener());

        checkPermissions();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // recyclerView
        getRanking("3");
        recyclerViewHomeRank = findViewById(R.id.recyclerView_home_rank);
        recyclerViewHomeRank.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHomeRank.setHasFixedSize(true);
        adapterRank = new RankListAdapter(ranks);
        recyclerViewHomeRank.setAdapter(adapterRank);

        getList("3");
        recyclerViewHomeList = findViewById(R.id.recyclerView_home_list);
        recyclerViewHomeList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHomeList.setHasFixedSize(true);

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

        recyclerViewHomeList.setAdapter(adapterList);

    }

    private void checkPermissions() {
        // 권한ID를 가져옵니다
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission4 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permission5 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission6 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission7 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED ||
                permission3 == PackageManager.PERMISSION_DENIED || permission4 == PackageManager.PERMISSION_DENIED ||
                permission5 == PackageManager.PERMISSION_DENIED || permission6 == PackageManager.PERMISSION_DENIED ||
                permission7 == PackageManager.PERMISSION_DENIED) {

            // 마시멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 다른 권한 요청
                requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);


                // 백그라운드 위치 액세스 권한을 요청
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // 사용자에게 권한 필요성을 설명하는 UI를 표시
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("백그라운드 위치 액세스 권한 필요");
                    builder.setMessage("앱의 다양한 기능을 사용하기 위해 권한이 필요합니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 권한 요청
                            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_BACKGROUND_PERMISSIONS_CODE);
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                } else {
                    // 권한 요청
                    requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_BACKGROUND_PERMISSIONS_CODE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            boolean allPermissionsGranted = true;

            // 모든 권한을 확인하여 허용 상태인지 확인
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // 모든 권한이 허용된 경우 처리할 로직
            } else {
                // 권한이 거부된 경우 처리할 로직
                Toast.makeText(this, "모든 권한을 허용해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == REQUEST_BACKGROUND_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 백그라운드 위치 액세스 권한이 허용된 경우 처리할 로직
            } else {
                // 백그라운드 위치 액세스 권한이 거부된 경우 처리할 로직
            }
        }
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        if(keycode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
            System.exit(0);
            return true;
        }
        return false;
    }
    private void getRanking(String limit){
        ranks.clear();
        try {
            String messages = new HttpTask().execute("#showRank", limit).get();
            String[] message = messages.split("/");


            for(int i = 0; i < message.length; i++){
                String id = message[i++];
                String point = message[i];
                Rank rank = new Rank(id, point);

                ranks.add(rank);
            }
            Rank.setCount(0);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void getList(String limit){
        reports.clear();
        try {
            String messages = new HttpTask().execute("#showList", limit, "-1").get();

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
    public void getColor() {
        try {
            String messages = new HttpTask().execute("#getColor").get();
            String[] message = messages.split("/");

            new MyColor(message[0], message[1], message[2]);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.imageButton_home_report:
                case R.id.button_home_report:
                    intent = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intent);
                    break ;
                case R.id.plusButton_home_list:
                case R.id.imageButton_home_list:
                    intent = new Intent(getApplicationContext(), ShowRListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.plusButton_home_rank:
                case R.id.imageButton_home_rank:
                    intent = new Intent(getApplicationContext(), RankListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.imageButton_mypage:
                    intent = new Intent(getApplicationContext(), MyPageActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    break;

            }
        }
    }
}

