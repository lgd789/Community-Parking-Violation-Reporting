package kr.ac.mmu;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class LocationHelper extends LocationCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private Context mContext;
    private Location lastLocation;

    public LocationHelper(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.mContext = context;
        startLocationUpdates();
    }

    public void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(1000);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, this, Looper.getMainLooper());
        }
    }

    public double getLatitude() {
        if (lastLocation != null) {
            return lastLocation.getLatitude();
        }
        else {
            return fetchLastKnownLocation().getLatitude();
        }
    }

    public double getLongitude() {
        if (lastLocation != null) {
            return lastLocation.getLongitude();
        }
        else {
            return fetchLastKnownLocation().getLongitude();
        }
    }

    private Location fetchLastKnownLocation() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {
                Location tempLocation = locationManager.getLastKnownLocation(provider);
                if (tempLocation != null && (location == null || tempLocation.getAccuracy() < location.getAccuracy())) {
                    location = tempLocation;
                }
            }
        }

        return location;
    }
    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        lastLocation = locationResult.getLastLocation();
    }
}
