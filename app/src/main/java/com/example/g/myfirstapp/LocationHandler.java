package com.example.g.myfirstapp;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;

/**
 * Created by G on 23-Mar-18.
 */

public class LocationHandler
{
    private static final int DISTANCE_TO_UPDATE = 20;
    private Activity activity;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    public LocationHandler(Fragment pFragment, LocationCallback pLmLocationCallback)
    {
        activity=pFragment.getActivity();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mLocationCallback=pLmLocationCallback;

    }

    public LocationHandler(MainActivity mainActivity, LocationCallback pLmLocationCallback)
    {
        activity=mainActivity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mLocationCallback=pLmLocationCallback;
    }

    public void stopUpdates() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public boolean start() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setSmallestDisplacement(DISTANCE_TO_UPDATE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            Toast.makeText(activity.getApplicationContext(), "Debes permitir la localización", Toast.LENGTH_LONG).show();
            return false;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        return true;
    }
}
