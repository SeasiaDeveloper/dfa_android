package com.dfa.maps;

/*
 * Created by admin on 10-01-2018.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class FusedLocationClass implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationListener {

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final static int FAST_LOCATION_FREQUENCY = 5 * 1000;
    private final static int LOCATION_FREQUENCY = 5 * 1000;

    /**
     * constructor
     *
     */
    public FusedLocationClass(Context mContext) {
        this.mContext = mContext;
        if (mContext != null)
            buildGoogleApiClient();
    }

    /**
     * destructor
     *
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stopLocationUpdates();
    }

    ///////////// 1

    /**
     * builds a GoogleApiClient
     */
    private synchronized void buildGoogleApiClient() {
        // setup googleApi client
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // setup location updates
        configRequestLocationUpdate();
    }

    ///////////// 2

    /**
     * config request location update
     */
    @SuppressLint("RestrictedApi")
    private void configRequestLocationUpdate() {
        mLocationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_FREQUENCY)
                .setFastestInterval(FAST_LOCATION_FREQUENCY);
    }

    ///////////// 3

    /**
     * request location updates
     *
     */
    private void requestLocationUpdates(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * start location updates
     *
     */
    private void startLocationUpdates(Context mContext) {
        // connect and force the updates
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            requestLocationUpdates(mContext);
        }
    }

    /**
     * removes location updates from the FusedLocationApi
     */
    private void stopLocationUpdates() {
        // stop updates, disconnect from google api
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    /**
     * get last available location
     *
     * @return last known location
     */
    @SuppressLint("MissingPermission")
    public Location getLastLocation(Context mContext) {



        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    // return last location


                     return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } else
                // return last location
                return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else
            startLocationUpdates(mContext); // start the updates
        return null;
    }


    /***********************************************************************************************
     * GoogleApiClient Callbacks
     **********************************************************************************************/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // do location updates
        if (mContext != null)
            requestLocationUpdates(mContext);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // connection to Google Play services was lost for some reason
        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect(); // attempt to establish a new connection
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /***********************************************************************************************
     * Location Listener Callback
     **********************************************************************************************/

    @Override
    public void onLocationChanged(Location location) {
    }

    public interface FusedLocationInterface {
        void onLocationChanged(Location location);
    }
}
