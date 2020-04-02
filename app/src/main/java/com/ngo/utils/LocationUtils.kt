package com.ngo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.ngo.ui.home.fragments.cases.view.LocationListenerCallback
import com.ngo.ui.home.fragments.home.view.HomeActivity
import javax.security.auth.callback.Callback

class LocationUtils(var activity: Context) : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLocation: Location
    private var mLocationManager: LocationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private val sUINTERVAL = (30 * 1000).toLong()  /* 10 secs */
    private val sFINTERVAL: Long = 5000 /* 2 sec */
    //private lateinit var locationManager: LocationManager

    // Code for make call back using interface from utility to activity
    private var listener: LocationListenerCallback?=null
    private var mlocationListener:LocationListenerCallback?=null

    override fun onLocationChanged(location: Location) {
//Call the function of interface define in Activity i.e MainActivity
        mlocationListener?.updateUi(location)
        //Toast.makeText(activity,"sd "+location.latitude,Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Utilities.showMessage(activity, connectionResult.errorMessage.toString())
        // pActivity.location.text = connectionResult.errorMessage.toString()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(pLocationChanged: Bundle?) {
        startLocationUpdates()
       // var pActivity=null
        //pActivity= ;
        val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            OnSuccessListener<Location> { location ->
                if (location != null) {
                    mLocation = location
                   // pCallback.updateUi(mLocation)
                }
            }
        }
    }

    fun initLocation(locationListener:LocationListenerCallback) {
       mlocationListener=locationListener;
        mGoogleApiClient = GoogleApiClient.Builder(activity).apply {
            addConnectionCallbacks(this@LocationUtils)
            addConnectionCallbacks(this@LocationUtils)
            addApi(LocationServices.API)
        }.build()
        mGoogleApiClient.connect()
        mLocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkLocation()
    }

    private fun checkLocation(): Boolean {
        if (!isLocationEnabled()) {
            showAlert(
                activity.getString(com.ngo.R.string.default_web_client_id),
                activity.getString(com.ngo.R.string.default_web_client_id)
            )
        }
        return isLocationEnabled()
    }


    fun isLocationEnabled(): Boolean {
        mLocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager!!.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }

    private fun showAlert(pTitle: String, pMessage: String) {
        val dialog = AlertDialog.Builder(activity)
        dialog.apply {
            setTitle(pTitle)
            setMessage(pMessage)
            setPositiveButton(activity.getString(com.ngo.R.string.default_web_client_id),
                { _, _ ->
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    activity.startActivity(myIntent)
                })
        }.show()
    }
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mLocationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = sUINTERVAL
                fastestInterval = sFINTERVAL
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
}}