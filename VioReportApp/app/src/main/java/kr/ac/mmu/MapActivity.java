package kr.ac.mmu;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private MapPoint.GeoCoordinate geoCoordinate;
    private MapPOIItem mapCenter = new MapPOIItem();
    private MapReverseGeoCoder rgeoCoder;
    private MapView.POIItemEventListener markerEvent = new MarkerEventListener();
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    private Button endMap, endBtn;
    private ImageButton curBtn, reportBtn;
    private TextView textView;

    ArrayList<MarkerData> dataArr = new ArrayList<MarkerData>();

    private RecyclerView recyclerViewRinfo;
    private ReportMapInfoAdapter adapterRinfo;
    List<Picture> pictures = new ArrayList<>();
    private View decorView;
    private int	uiOption;
    private String reportId;
    MarkerData centerPoint = new MarkerData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility( uiOption );
//
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("키해시는 :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        //지도
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setPOIItemEventListener(markerEvent);

        // 줌 레벨 변경
        mapView.setZoomLevel(1, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);

        // DB에서 위치정보 받음
        ArrayList<MarkerData> dataArr = getData();
        for (MarkerData data : dataArr) {
            MapPOIItem marker = new MapPOIItem();

            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(data.getLatitude(), data.getLongitude()));
            marker.setItemName(data.getName());
            if (data.getReportCount() == 2) {
                marker.setMarkerType(MapPOIItem.MarkerType.RedPin);
            }
            else if (data.getReportCount() == 3) {
                marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
            }
            else if (data.getReportCount() >= 4) {
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            }

            mapView.addPOIItem(marker);
        }
        // 지도 종료
        endMap = (Button) findViewById(R.id.end_btn);
        endMap.bringToFront();
        endMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 내 위치
        curBtn = findViewById(R.id.cur_btn);
        curBtn.bringToFront();
        curBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            }
        });

        //신고하기 버튼
        reportBtn = findViewById(R.id.report_btn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });
        reportBtn.bringToFront();
        textView = (TextView) findViewById(R.id.textView);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        View layoutMain = findViewById(R.id.layout_main);
        layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "hi", Toast.LENGTH_SHORT);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        recyclerViewRinfo = findViewById(R.id.recyclerView_MapRinfo);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewRinfo.setLayoutManager(manager);
        recyclerViewRinfo.setHasFixedSize(true);
        recyclerViewRinfo.addItemDecoration(new PhOffsetItemDecoration(0,0,30,30));
    }
    // DB에서 위치정보 받을 부분
    public ArrayList<MarkerData> getData(){
        String messages = null;
        try {
            messages = new HttpTask().execute("#map").get();

            if (!messages.equals("false")) {
                String[] message = messages.split("/");

                int i = 0;
                while (true) {
                    String carNumber = message[i++];
                    Double latitude = Double.valueOf(message[i++]);
                    Double longitude = Double.valueOf(message[i++]);
                    String reportId = message[i++];
                    Integer reportCount = Integer.valueOf(message[i++]);

                    MarkerData data = new MarkerData(reportId, carNumber, latitude, longitude, reportCount);

                    dataArr.add(data);

                    if (i >= message.length - 1) break;
                }
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return dataArr;
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        geoCoordinate = mapPoint.getMapPointGeoCoord();
        centerPoint.setLatitude(geoCoordinate.latitude);
        centerPoint.setLongitude(geoCoordinate.longitude);

        Log.e("지도 중심 : 위도","================" + centerPoint.getLatitude());
        Log.e("지도 중심 : 경도","================" + centerPoint.getLongitude());
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

//        mapView.removePOIItem(mapCenter);
//
//        mapCenter.setMapPoint(MapPoint.mapPointWithGeoCoord(centerPoint.getLatitude(), centerPoint.getLongitude()));
//        mapCenter.setItemName("내 위치");
//        mapCenter.setMarkerType(MapPOIItem.MarkerType.BluePin);
//        mapView.addPOIItem(mapCenter);

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
                textView.setText(centerPoint.getName());
                Log.e("지도 중심 : 도로명","================" + centerPoint.getName());

            }

            @Override
            public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
                centerPoint.setName("NULL");
                textView.setText(centerPoint.getName());
                Log.e("지도 중심 : 도로명","================" + centerPoint.getName());
            }
        }, this);

        rgeoCoder.startFindingAddress();
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    class MarkerEventListener implements MapView.POIItemEventListener {

        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            double latitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
            double longitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;

            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            String itemName = mapPOIItem.getItemName();
            reportId = itemName.split("/")[0];

            pictures.clear();
            httptask();

            adapterRinfo = new ReportMapInfoAdapter(pictures);
            recyclerViewRinfo.setAdapter(adapterRinfo);
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    }

    private void httptask(){
        try {
            String messages = new HttpTask().execute("#showInfo", reportId).get();


            String[] message = messages.split("/");
            Log.e("showInfoLog", messages);
            for (int i = 0; i < message.length; i++) {
                String imagePath = message[i++];
                String reportId = message[i++];
                String reporter = message[i++];
                String reportTime = message[i];
                int reportCount = (i/4) + 1;


                // Bitmap image = new DownloadImageTask().execute(imagePath).get();
                Picture picture = new Picture(imagePath, reportId, reporter, reportTime, reportCount);
                pictures.add(picture);
            }


        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}