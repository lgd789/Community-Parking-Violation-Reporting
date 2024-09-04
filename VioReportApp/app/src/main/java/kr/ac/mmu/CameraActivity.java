package kr.ac.mmu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity  implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private Uri image_uri = null;
    private String imagePath = "";
    private String address = "";
    private MarkerData centerPoint = new MarkerData();
    private MapReverseGeoCoder rgeoCoder;
    private LocationHelper location;
    Double latitude, longitude;
    Boolean check = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = new LocationHelper(this);


        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, getNowTime24());
            values.put(MediaStore.Images.Media.DISPLAY_NAME, getNowTime24() + ".jpg");

            image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
            cameraResultLauncher.launch(cameraIntent);




        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    ActivityResultLauncher<Intent> cameraResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {

                                imagePath = getImagePathFromUri(image_uri);
                                String message = null;

                                Log.e("123456", "start");

                                SharedPreferences sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                // 위도 경도 이미지
                                String id = sp.getString("userId", null);
                                boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);



                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                rgeoCoder = new MapReverseGeoCoder("c39d4ee94927124c8da7d300a6af1d78",
                                        MapPoint.mapPointWithGeoCoord(latitude, longitude), (MapReverseGeoCoder.ReverseGeoCodingResultListener) new MapReverseGeoCoder.ReverseGeoCodingResultListener() {

                                    @Override
                                    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
                                        address = s;
                                        Log.e("httpppp2", String.valueOf(latitude) + address);

                                        try {
                                            new HttpTask().execute("#capture_info", id, String.valueOf(latitude), String.valueOf(longitude), address, imagePath).get();
                                            new HttpTask().execute("#capture", imagePath).get();
                                        } catch (ExecutionException e) {
                                            throw new RuntimeException(e);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }

                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        //                                intent.putExtra("carNum", message);
                                        //                                intent.putExtra("imagePath", imagePath);

                                        startActivity(intent);
                                        finish();
                                    }
                                    @Override
                                    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

                                    }
                                }, CameraActivity.this);
                                rgeoCoder.startFindingAddress();


                            }
                        }
                    }
            );

    public String getImagePathFromUri(Uri uri){
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String imagePath = cursor.getString(column_index);
            cursor.close();


            return imagePath;
        } else {
            return uri.getPath();
        }
    }
    public static String getNowTime24() {
        long time = System.currentTimeMillis();

        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String str = dayTime.format(new Date(time));

        return str;
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
