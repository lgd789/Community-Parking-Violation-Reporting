package kr.ac.mmu;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.concurrent.ExecutionException;

public class ReportActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private MapPoint.GeoCoordinate geoCoordinate;
    private MapPOIItem mapCenter = new MapPOIItem();
    private MapReverseGeoCoder rgeoCoder;

    private TextView textViewCur;
    private EditText editTextCarNum;
    private Spinner spinnerReportType;
    private Button buttonSend;
    private ImageButton imageButtonReportBack;
    private RadioGroup radioGroupReportType;
    private Button[] buttonReportType = new Button[6];
    private Integer[] reportTypeId = {R.id.button_fireplug, R.id.button_bus, R.id.button_corner, R.id.button_crosswalk, R.id.button_childrenzone, R.id.button_more};


    private String reportType;
    private MarkerData centerPoint = new MarkerData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Intent intent = getIntent();
        String carNum = intent.getStringExtra("carNum");
        String imagePath = intent.getStringExtra("imagePath");

        editTextCarNum = findViewById(R.id.editTextCarNum);
        textViewCur = findViewById(R.id.textViewCur);
//        spinnerReportType = findViewById(R.id.spinnerReportType);
        buttonSend = findViewById(R.id.buttonSend);

        findViewById(R.id.imageButton_report_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LocationHelper location = new LocationHelper(this);
        centerPoint.setLatitude(location.getLatitude());
        centerPoint.setLongitude(location.getLongitude());

        //지도
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_report_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener((MapView.MapViewEventListener) this);

        mapView.setZoomLevel((int) mapView.MIN_ZOOM_LEVEL, true);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        mapCenter.setMapPoint(MapPoint.mapPointWithGeoCoord(centerPoint.getLatitude(), centerPoint.getLongitude()));
        mapCenter.setItemName("내 위치");
        mapCenter.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mapView.addPOIItem(mapCenter);

        rgeoCoder = new MapReverseGeoCoder("c39d4ee94927124c8da7d300a6af1d78",
                MapPoint.mapPointWithGeoCoord(centerPoint.getLatitude(), centerPoint.getLongitude()), new MapReverseGeoCoder.ReverseGeoCodingResultListener() {

            @Override
            public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
                centerPoint.setName(s);
                textViewCur.setText(centerPoint.getName());
                Log.e("지도 중심 : 도로명","================" + s);

            }
            @Override
            public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

            }
        }, this);
        rgeoCoder.startFindingAddress();

        //입력하면 변경 추가해야함
        editTextCarNum.setText(carNum);

//        // Spinner
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.report_type, android.R.layout.simple_spinner_dropdown_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerReportType.setAdapter(adapter);
//
//        spinnerReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                reportType = parent.getItemAtPosition(position).toString();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        for (int i=0; i<reportTypeId.length; i++) {
            buttonReportType[i] = findViewById(reportTypeId[i]);
            if (i==0) {
                reportType = buttonReportType[i].getText().toString();
                buttonReportType[i].setBackgroundColor(Color.parseColor("#2700FF19"));
            }
            
            buttonReportType[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportBtnfocus(v.getId());
                }
            });
        }


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 차량번호, 신고유형, 이미지주소(시간), 위도, 경도, 도로명
                try {
                    SharedPreferences sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    
                    String id = sp.getString("userId", null);
                    boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);
                    String carNumber = editTextCarNum.getText().toString();
                    String messages;

                    if (isLoggedIn) {
                        messages = new HttpTask().execute("#report", id, carNumber, reportType, imagePath,
                                centerPoint.getLatitude().toString(), centerPoint.getLongitude().toString(), centerPoint.getName()).get();

                        String[] error = messages.split("/");
                        if (error[0].equals("timeError")) {
                            Toast.makeText(ReportActivity.this, error[1] + "초 후에 다시 촬영해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        messages =  new HttpTask().execute("#report", "", carNumber, reportType, imagePath,
                                centerPoint.getLatitude().toString(), centerPoint.getLongitude().toString(), centerPoint.getName()).get();
                    }

                    // message체크 해야함

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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
        geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
        centerPoint.setLatitude(geoCoordinate.latitude);
        centerPoint.setLongitude(geoCoordinate.longitude);

        Log.e("지도 중심 : 위도","================" + centerPoint.getLatitude());
        Log.e("지도 중심 : 경도","================" + centerPoint.getLongitude());

        mapView.removePOIItem(mapCenter);

        mapCenter.setMapPoint(MapPoint.mapPointWithGeoCoord(centerPoint.getLatitude(), centerPoint.getLongitude()));
        mapCenter.setItemName("내 위치");
        mapCenter.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mapView.addPOIItem(mapCenter);
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
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        Log.e("onMapViewDragEnded", "start");
        rgeoCoder = new MapReverseGeoCoder("c39d4ee94927124c8da7d300a6af1d78",
                MapPoint.mapPointWithGeoCoord(centerPoint.getLatitude(), centerPoint.getLongitude()), new MapReverseGeoCoder.ReverseGeoCodingResultListener() {

            @Override
            public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
                centerPoint.setName(s);
                textViewCur.setText(centerPoint.getName());
                Log.e("지도 중심 : 도로명","================" + centerPoint.getName());

            }

            @Override
            public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
                centerPoint.setName("NULL");
                textViewCur.setText(centerPoint.getName());
                Log.e("지도 중심 : 도로명","================" + centerPoint.getName());
            }
        }, this);

        rgeoCoder.startFindingAddress();
    }


    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    public void reportBtnfocus(int id) {
        for (int i=0; i<reportTypeId.length; i++) {
            if (reportTypeId[i] == id) {
                reportType = buttonReportType[i].getText().toString();
                buttonReportType[i].setBackgroundColor(Color.parseColor("#2700FF19"));
            }
            else {
                buttonReportType[i].setBackgroundColor(Color.parseColor("#00000000"));
            }
        }
    }
}