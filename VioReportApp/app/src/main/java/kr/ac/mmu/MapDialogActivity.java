//package kr.ac.mmu;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.app.Dialog;
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import net.daum.mf.map.api.MapPOIItem;
//import net.daum.mf.map.api.MapPoint;
//import net.daum.mf.map.api.MapView;
//
//public class MapDialogActivity extends Dialog implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
//    private ImageButton shutdownClick;
//    private TextView test;
//    private MapView mapView;
//    private ViewGroup mapViewContainer;
//    private MapPOIItem mapCenter = new MapPOIItem();
//    private String latitude, longitude;
//
//    public MapDialogActivity(@NonNull Context context, String contents) {
//        super(context);
//        setContentView(R.layout.activity_map_dialog);
//
//        String location[] = contents.split("/");
//
//        //지도
//        mapView = new MapView(context);
//        mapViewContainer = (ViewGroup) findViewById(R.id.map_dialog_view);
//        mapViewContainer.addView(mapView);
//        mapView.setMapViewEventListener((MapView.MapViewEventListener) this);
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
//
//        mapCenter.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(location[0]),  Double.parseDouble(location[1])));
//        mapCenter.setItemName("차량위치");
//        mapCenter.setMarkerType(MapPOIItem.MarkerType.BluePin);
//        mapView.addPOIItem(mapCenter);
//
//        shutdownClick = findViewById(R.id.imageButton_shutdown);
//        shutdownClick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//
//    }
//
//    @Override
//    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
//
//    }
//
//    @Override
//    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
//
//    }
//
//    @Override
//    public void onCurrentLocationUpdateFailed(MapView mapView) {
//
//    }
//
//    @Override
//    public void onCurrentLocationUpdateCancelled(MapView mapView) {
//
//    }
//
//    @Override
//    public void onMapViewInitialized(MapView mapView) {
//
//    }
//
//    @Override
//    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
//
//    }
//
//    @Override
//    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
//
//    }
//}