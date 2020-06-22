package com.dfa.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.dfa.ui.home.fragments.home.view.HomeActivity

class LocationUpdateService : Service() {
    private val PACKAGE_NAME =
        "com.ngo"

    private val TAG = LocationUpdatesService()::class.java.simpleName

    /**
     * The name of the channel for notifications.
     */
    private val CHANNEL_ID = "channel_01"

    public val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"

    val EXTRA_LOCATION = "$PACKAGE_NAME.location"
    private val EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification"

    private val mBinder: IBinder = LocalBinder()

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 30000

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
        UPDATE_INTERVAL_IN_MILLISECONDS / 2

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private val NOTIFICATION_ID = 12345678

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private var mChangingConfiguration = false

    private var mNotificationManager: NotificationManager? = null

    /**
     * Contains parameters used by [com.google.android.gms.location.FusedLocationProviderApi].
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null

    private var mServiceHandler: Handler? = null

    /**
     * The current location.
     */
    private var mLocation: Location? = null

    fun LocationUpdatesService() {}

    override fun onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                //onNewLocation(locationResult.lastLocation,this)
            }
        }
        createLocationRequest()
        getLastLocation()
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(com.dfa.R.string.app_name)
            // Create the channel for the notification
            val mChannel =
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            // Set the Notification Channel for the Notification Manager.
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service started")
        val startedFromNotification = intent.getBooleanExtra(
            EXTRA_STARTED_FROM_NOTIFICATION,
            false
        )
        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates()
            //stopSelf(); //now
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent?): IBinder? { // Called when a client (MainActivity in case of this sample) comes to the foreground
// and binds with this service. The service should cease to be a foreground service
// when that happens.
        Log.i(TAG, "in onBind()")
        // stopForeground(true);
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent?) { // Called when a client (MainActivity in case of this sample) returns to the foreground
// and binds once again with this service. The service should cease to be a foreground
// service when that happens.
        Log.i(TAG, "in onRebind()")
        //stopForeground(true);
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Last client unbound from service")
        // Called when the last client (MainActivity in case of this sample) unbinds from this
// service. If this method is called due to a configuration change in MainActivity, we
// do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && LocationUpdateUtils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service")
            startForeground(NOTIFICATION_ID, getNotification())
        }
        return true // Ensures onRebind() is called when a client re-binds.
    }

    override fun onDestroy() {
        mServiceHandler!!.removeCallbacksAndMessages(null)
        val broadcastIntent = Intent()
        broadcastIntent.action = ACTION_BROADCAST
       // broadcastIntent.setClass(this, HomeActivity.MyReceiver::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    fun requestLocationUpdates(context: Context) {
        Log.i(TAG, "Requesting location updates")
        //LocationUpdateUtils.setRequestingLocationUpdates(this, true)
        context.startService(Intent(context, LocationUpdatesService()::class.java))
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    onNewLocation(locationResult.lastLocation,context)
                }
            }
            createLocationRequest()
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
           // LocationUpdateUtils.setRequestingLocationUpdates(this, false)
            Log.e(
                TAG,
                "Lost location permission. Could not request updates. $unlikely"
            )
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
             mFusedLocationClient?.removeLocationUpdates(mLocationCallback);
             LocationUpdateUtils.setRequestingLocationUpdates(this, false)
            //stopSelf();
        } catch (unlikely: SecurityException) {
           // LocationUpdateUtils.setRequestingLocationUpdates(this, true)
            Log.e(
                TAG,
                "Lost location permission. Could not remove updates. $unlikely"
            )
        }
    }

    /**
     * Returns the [NotificationCompat] used as part of the foreground service.
     */
    private fun getNotification(): Notification? {
        val intent = Intent(this, LocationUpdatesService()::class.java)
        val text: String? = LocationUpdateUtils.getLocationText(mLocation)
        Toast.makeText(
            this, "hello$text",
            Toast.LENGTH_SHORT
        ).show()
        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)
        // The PendingIntent that leads to a call to onStartCommand() in this service.
        val servicePendingIntent = PendingIntent.getService(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // The PendingIntent to launch activity.
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, HomeActivity()::class.java), 0
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .addAction(
                com.dfa.R.drawable.ic_launcher_foreground,
                getString(com.dfa.R.string.launch_activity),
                activityPendingIntent
            )
            .addAction(
                com.dfa.R.drawable.ic_close_black_24dp,
                getString(com.dfa.R.string.remove_location_updates),
                servicePendingIntent
            )
            .setContentText(text)
            .setContentTitle(LocationUpdateUtils.getLocationTitle(this))
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(com.dfa.R.mipmap.ic_launcher)
            .setTicker(text)
            .setWhen(System.currentTimeMillis())
        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }
        return builder.build()
    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient!!.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
                    } else {
                        Log.w(TAG, "Failed to get location.")
                    }
                }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.$unlikely")
        }
    }

    private fun onNewLocation(location: Location,context: Context) {
        Log.i(TAG, "New location: $location")
        mLocation = location
        // Notify anyone listening for broadcasts about the new location.
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(context)) {
            mNotificationManager!!.notify(NOTIFICATION_ID, getNotification())
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) { /*    Intent intent = new Intent(ACTION_BROADCAST);
        sendBroadcast(intent);*/
        val broadcastIntent = Intent()
        broadcastIntent.action = ACTION_BROADCAST
       // broadcastIntent.setClass(this, HomeActivity.MyReceiver::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onTaskRemoved(rootIntent)
    }

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    class LocalBinder : Binder() {
        /*val service: LocationUpdateService
            get() = LocationUpdateService()*/
        fun getService():LocationUpdateService? {
            return LocationUpdateService()
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The [Context].
     */
    fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager = context.getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }
}