package kr.ac.mmu;


import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ShowRInfoActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener{
    private CmtAdapter adapterCmt;
    private RecyclerView recyclerViewCmt;
    private TextView textViewMaxCount;

    private ImageButton imageButtonRlistBack, imageButtonReg, plusButtonInfoAdd, mapButtonInfo;
    private RecyclerView recyclerViewRinfo;
    private EditText editTextComment;
    private ReportRinfoAdapter adapterRinfo;
    private String reportId, reportCount, latitude, longitude;
    List<Picture> pictures = new ArrayList<>();
    List<Cmt> cmts = new ArrayList<>();

    MapView mapView;
    ViewGroup mapViewContainer;
    MapPOIItem mapCenter = new MapPOIItem();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String id;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_rinfo);



        Intent intent = getIntent();
        reportId = intent.getStringExtra("reportId");
        reportCount = intent.getStringExtra("reportCount");
        latitude = intent.getStringExtra("reportLatitude");
        longitude = intent.getStringExtra("reportLongitude");

        sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
        editor = sp.edit();
        id = sp.getString("userId", null);
        isLoggedIn = sp.getBoolean("isLoggedIn", false);

        // 추가 신고 +버튼
        plusButtonInfoAdd = findViewById(R.id.plusButton_info_add);
        plusButtonInfoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });

        // 맵 팝업창
        mapButtonInfo = findViewById(R.id.mapButton_info);
        mapButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        setMaxCountText(reportCount);

        recyclerViewCmt = findViewById(R.id.recyclerView_rinfo_cmt);
        recyclerViewCmt.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCmt.setHasFixedSize(true);
        adapterCmt = new CmtAdapter(cmts);
        recyclerViewCmt.setAdapter(adapterCmt);

        // 뒤로가기 버튼
        imageButtonRlistBack = findViewById(R.id.imageButton_rlist_back);
        imageButtonRlistBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerViewRinfo = findViewById(R.id.recyclerView_Rinfo);
        recyclerViewRinfo.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRinfo.setHasFixedSize(true);

        adapterRinfo = new ReportRinfoAdapter(pictures, id);
        recyclerViewRinfo.setAdapter(adapterRinfo);

        adapterRinfo.setOnItemClickListener(new ReportRinfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String reportId) {
                try {
                    new HttpTask().execute("#deleteReport", reportId).get();

                    httptask();

                    reportCount = String.valueOf(Integer.parseInt(reportCount)-1);
                    setMaxCountText(reportCount);

                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // httptask();
        editTextComment = findViewById(R.id.editText_commend);
        imageButtonReg = findViewById(R.id.imageButton_reg);
        imageButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String content = String.valueOf(editTextComment.getText());
                    if (!content.equals("")) {
                        if (isLoggedIn) {
                            Log.e("comment", reportId + id + content);
                            String messages = new HttpTask().execute("#commentReg", reportId, id, content).get();
                        } else {
                            String messages = new HttpTask().execute("#commentReg", reportId, "", content).get();
                        }
                        editTextComment.setText("");

//                        Intent intent = new Intent(getApplicationContext(), ShowRInfoActivity.class);
//                        intent.putExtra("reportId", reportId);
//                        intent.putExtra("reportCount", reportCount);
//                        intent.putExtra("reportLatitude", latitude);
//                        intent.putExtra("reportLongitude", longitude);
//
//                        startActivity(intent);
//                        finish();
                        getCmt();
                    }

                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        getCmt();

    }
    @Override
    protected void onResume() {
        super.onResume();
        httptask();
    }

    private void setMaxCountText(String reportCount) {
        textViewMaxCount = findViewById(R.id.text_info_maxcount);
        textViewMaxCount.setText(reportCount + "차신고 진행중");
        if (Integer.valueOf(reportCount) == 2) {
            textViewMaxCount.setTextColor(Color.parseColor(MyColor.getReportColor1()));
        }
        else if (Integer.valueOf(reportCount) == 3) {
            textViewMaxCount.setTextColor(Color.parseColor(MyColor.getReportColor2()));
        }
        else if (Integer.valueOf(reportCount) > 3) {
            textViewMaxCount.setTextColor(Color.parseColor(MyColor.getReportColor3()));
        }
    }
    private void httptask(){
        pictures.clear();

        try {

            float now = System.currentTimeMillis();
            String messages = new HttpTask().execute("#showInfo", reportId).get();
            float now1 = System.currentTimeMillis();
            if (messages.equals("false")) {
                finish();
                return;
            }
            String[] message = messages.split("/");
            Log.e("messages", messages);

            for (int i=0; i<message.length; i++) {
                String imagePath = message[i++];
                String reportId = message[i++];
                String reporter = message[i++];
                String reportTime = message[i];
                int reportCount = (i/4) + 1;
                // Bitmap image = new DownloadImageTask().execute(imagePath).get();
                Picture picture = new Picture(imagePath, reportId, reporter, reportTime, reportCount);
                pictures.add(picture);
            }
            float now2 = System.currentTimeMillis();
            float mils2 = now2 - now;
            float mils1 = now1 - now;

            adapterRinfo.notifyDataSetChanged();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void getCmt(){
        try {
            cmts.clear();
            String messages = new HttpTask().execute("#showComment", reportId).get();

            if (!messages.equals("false")) {
                String[] message = messages.split("/");


                for (int i = 0; i < message.length; i++) {
                    String id = message[i++];
                    String str = message[i++];
                    String time = message[i];
                    Cmt cmt = new Cmt(id, str, time.substring(11, time.length()-3));

                    cmts.add(cmt);
                }
                adapterCmt.notifyDataSetChanged();
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_map_dialog, findViewById(R.id.dialog_map));

        //지도
        mapView = new MapView(this);
        mapViewContainer = view.findViewById(R.id.map_dialog_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener((MapView.MapViewEventListener) this);

        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(latitude), Double.parseDouble(longitude)), true);
        mapCenter.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(latitude),  Double.parseDouble(longitude)));
        mapCenter.setItemName("차량위치");
        mapCenter.setMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(mapCenter);

        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.imageButton_shutdown).bringToFront();
        view.findViewById(R.id.imageButton_shutdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

        WindowManager.LayoutParams params=alertDialog.getWindow().getAttributes();
        params.width=1000;
        params.height=1000;
        alertDialog.getWindow().setAttributes(params);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}