package com.ngo.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.OnSuccessListener
import com.ngo.ui.home.fragments.cases.view.LocationListenerCallback
import java.util.*

class LocationSecondUtility() : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    val REQUEST_CHECK_SETTINGS = 738
    private val REQUEST_FIND_LOCATION = 562
    private val INTERVAL: Long = 5000
    private val FASTEST_INTERVAL = INTERVAL
    private val SMALLEST_DISPLACEMENT = 0.25f //quarter of a meter
    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mCurrentLocation: Location? = null
    private var context: Context? = null
    private var mLocationManager: LocationManager? = null
    private var listener: LocationListenerCallback? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null


    public fun LocationUtils(
        context: Context?,
        listener: LocationListenerCallback?
    ) {
        this.listener = listener
        this.context = context
        createLocationRequest()
        mGoogleApiClient = GoogleApiClient.Builder(context!!)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        if (Build.VERSION.SDK_INT < 23) {
            mGoogleApiClient?.connect()
        } else {
            checkPermissions()
        }
        mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }

    /**
     * Create Location Request
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = INTERVAL
        mLocationRequest!!.fastestInterval = FASTEST_INTERVAL
        mLocationRequest!!.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Method for check Runtime permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        val accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        val accessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
        val hasAccessFineLocation: Int =
            ContextCompat.checkSelfPermission(context!!, accessFineLocation)
        val hasAccessCoarseLocation: Int =
            ContextCompat.checkSelfPermission(context!!, accessCoarseLocation)
        val permissions: MutableList<String> =
            ArrayList()
        if (hasAccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            permissions.add(accessFineLocation)
        }
        if (hasAccessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            permissions.add(accessCoarseLocation)
        }
        if (!permissions.isEmpty()) {
            val params = permissions.toTypedArray()
            //listener.onRequestPermission(params)
            //((Activity) context).requestPermissions(params, REQUEST_FIND_LOCATION);
        } else {
            mGoogleApiClient!!.connect()
        }
    }

    /**
     * Overridden method for onRequestPermissionResults
     *
     * @param requestCode
     * @param grantResults
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_FIND_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleApiClient!!.connect()
                } else {
                    findLocation(false)
                }
            }
        }
    }

    /**
     * Method to set  the location enabled
     */
    private fun setGPSEnabled() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            val state = result.locationSettingsStates
            when (status.statusCode) {
                /* LocationSettingsStatusCodes.SUCCESS -> findLocation(true)
                 LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->  //status.startResolutionForResult(Login.this, 1000);
                     //listener.requestGPS(status, REQUEST_CHECK_SETTINGS)
                     LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE
                 -> {
                 }*/
            }
        }
    }


    override fun onConnected(bundle: Bundle?) {
        setGPSEnabled()
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) { // TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
            return
        }

        findLocation(true)
   /*     LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            mLocationRequest, this
        )*/

        // val fusedLocationProviderClient: FusedLocationProviderClient =

    }

    /**
     * Method for get current location
     */
    private fun findLocation(permissionsGranted: Boolean) {
        mCurrentLocation = null
        if (permissionsGranted) {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED
            ) { // TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
                return
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)


            mLocationRequest = LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(1000);

            var builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest!!)

        /*    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, listener, Looper.myLooper());*/


        val fusedLocationProviderClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context!!)
           /* fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                OnSuccessListener<Location> { location ->
                    if (location != null) {
                      //  mLocation = location
                        listener?.updateUi(location)
                    }
                }
            }*/
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                OnSuccessListener<Location> { location ->
                    if (location != null) {
                        //  mLocation = location
                        listener?.updateUi(location)
                    }
                }
            }
        }
        if (mCurrentLocation != null) {
            val latitude = mCurrentLocation!!.latitude
            val longitude = mCurrentLocation!!.longitude
            // ProgressUtility.INSTANCE.dismissProgress()
            listener?.updateUi(mCurrentLocation!!)
        } else {
            // ProgressUtility.INSTANCE.dismissProgress()
            listener?.onLocationNotFound()
        }
    }

    override fun onConnectionSuspended(i: Int) {
        Log.d("onConnectionSuspended()", "Google APi connection Suspended")
        mGoogleApiClient!!.connect()
    }

    /*override fun onConnectionFailed(connectionResult: ConnectionResult?) {
        Log.d("onConnectionFailed()", "Google APi connection Failed")
    }*/


    fun onActivityResult(requestCode: Int, resultCode: Int) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val handler = Handler()
                    handler.postDelayed({ findLocation(true) }, 3000)
                }
                Activity.RESULT_CANCELED -> findLocation(false)
                else -> findLocation(false)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        mCurrentLocation = location
        listener?.updateUi(location) //LatLng(location.latitude, location.longitude)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("onConnectionFailed()", "Google APi connection Failed")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}