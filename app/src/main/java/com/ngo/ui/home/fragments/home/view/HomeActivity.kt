package com.ngo.ui.home.fragments.home.view


import android.Manifest
import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.ngo.R
import com.ngo.adapters.TabLayoutAdapter
import com.ngo.base.BaseActivity
import com.ngo.pojo.response.GetProfileResponse
import com.ngo.ui.earnings.view.MyEarningsActivity
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.home.fragments.cases.CasesFragment
import com.ngo.ui.home.fragments.cases.view.LocationListenerCallback
import com.ngo.ui.home.fragments.home.presenter.HomePresenter
import com.ngo.ui.home.fragments.home.presenter.HomePresenterImpl
import com.ngo.ui.home.fragments.photos.view.PhotosFragment
import com.ngo.ui.home.fragments.videos.view.VideosFragment
import com.ngo.ui.login.view.LoginActivity
import com.ngo.ui.mycases.MyCasesActivity
import com.ngo.ui.profile.ProfileActivity
import com.ngo.ui.termsConditions.view.TermsAndConditionActivity
import com.ngo.ui.updatepassword.view.GetLogoutDialogCallbacks
import com.ngo.ui.updatepassword.view.UpdatePasswordActivity
import com.ngo.utils.*
import com.ngo.utils.alert.AlertDialog
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.nav_header.*


class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, HomeView,
    GetLogoutDialogCallbacks, LocationListenerCallback, OnSharedPreferenceChangeListener {
    private var mDrawerLayout: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null
    private var mToolbar: Toolbar? = null
    private var homePresenter: HomePresenter = HomePresenterImpl(this)
    private var authorizationToken: String? = null
    private var preferencesHelper: PreferenceHandler? = null
    private lateinit var locationManager: LocationManager
    private lateinit var locationUtils: LocationUtils
    //location
    private var TAG: String = HomeActivity::class.java.getSimpleName()
    // Used in checking for runtime permissions.
    private var REQUEST_PERMISSIONS_REQUEST_CODE = 34
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private var myReceiver: MyReceiver? = null
    // A reference to the service used to get location updates.
    private var mService: LocationUpdateService? = null
    // Tracks the bound state of the service.
    private var mBound = false
    lateinit var mServiceConnection: ServiceConnection
    // UI elements.
    private var mRequestLocationUpdatesButton: Button? = null
    private var mRemoveLocationUpdatesButton: Button? = null
    // Monitors the state of the connection to the service.


    override fun getLayout(): Int {
        return R.layout.home_activity
    }

    override fun onResume() {
        super.onResume()
        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        homePresenter.hitProfileApi(authorizationToken)
        //location
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver!!,
            IntentFilter(LocationUpdateUtils.ACTION_BROADCAST)
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver!!)
        super.onPause()
    }

    override fun onStop() {
        if (mBound) { // Unbind from the service. This signals to the service that this activity is no longer
// in the foreground, and the service can respond by promoting itself to a foreground
// service.
            unbindService(mServiceConnection) //now
            mBound = false
        }
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    override fun setupUI() {
        mToolbar = findViewById<View>(R.id.nav_action) as Toolbar
        setSupportActionBar(mToolbar)

        mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout!!.addDrawerListener(mToggle!!)
        mToggle!!.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        }

        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.addFragment(GeneralPublicHomeFragment(), "Home")
        adapter.addFragment(CasesFragment(), "Cases")
        adapter.addFragment(PhotosFragment(), "Photo(s)")
        adapter.addFragment(VideosFragment(), "Video(s)")
        viewPager?.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        nav_view?.setNavigationItemSelectedListener(this)
        //location
        /*  locationUtils = LocationSecondUtility()
          locationUtils.LocationUtils(this, this, this)*/
        /* locationUtils=LocationUtils(this)
         locationUtils.initLocation()*/
        myReceiver = MyReceiver()

        mServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder: LocationUpdateService.LocalBinder =
                    service as LocationUpdateService.LocalBinder
                mService = binder.getService()
              //  mService!!.requestLocationUpdates()
                mBound = true
            }

            override fun onServiceDisconnected(name: ComponentName) {
                mService = null
                mBound = false
            }
        }

        // Check that the user hasn't revoked permissions by going to Settings.
        // Check that the user hasn't revoked permissions by going to Settings.
        if (LocationUpdateUtils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions()
            }
        }

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            mService?.requestLocationUpdates(this)
        }
    }

    override fun onStart() {
        super.onStart()
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            mService?.requestLocationUpdates(this)
        }
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(
            Intent(this, LocationUpdateService::class.java), mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            /*Log.i(
                TAG,
                "Displaying permission rationale to provide additional context."
            )
            Snackbar.make(
                findViewById(R.id.home),
                R.string.permission_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()*/
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
// sets the permission in a given state or the user denied the permission
// previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) { // If user interaction was interrupted, the permission request is cancelled and you
// receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission was granted.
                mService!!.requestLocationUpdates(this)
            } else { // Permission denied.

            }
        }
    }

    /**
     * Receiver for broadcasts sent by [LocationUpdatesService].
     */
    class MyReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val location =
                intent.getParcelableExtra<Location>(LocationUpdateUtils.EXTRA_LOCATION)
            if (location != null) {
                Utilities.showMessage(
                    context,
                    "hello" + LocationUpdateUtils.getLocationText(location)
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, LocationUpdateService::class.java))
                Utilities.showMessage(
                    context,
                    "hello" + LocationUpdateUtils.getLocationText(location)
                )
            } else {
                context.startService(Intent(context, LocationUpdateService::class.java))
                Utilities.showMessage(
                    context,
                    "hello" + LocationUpdateUtils.getLocationText(location)
                )
            }
        }
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        s: String
    ) { // Update the buttons state depending on whether location updates are being requested.
        if (s == LocationUpdateUtils.KEY_REQUESTING_LOCATION_UPDATES) {
          /*  setButtonsState(
                sharedPreferences.getBoolean(
                    LocationUpdateUtils.KEY_REQUESTING_LOCATION_UPDATES,
                    false
                )
            )*/
        }
    }

  /*  private fun setButtonsState(requestingLocationUpdates: Boolean) {
        if (requestingLocationUpdates) {
            mRequestLocationUpdatesButton!!.isEnabled = false
            mRemoveLocationUpdatesButton!!.isEnabled = true
        } else {
            mRequestLocationUpdatesButton!!.isEnabled = true
            mRemoveLocationUpdatesButton!!.isEnabled = false
        }
    }*/

    private fun loadNavHeader(getProfileResponse: GetProfileResponse) { // name, wegbsite
        textName.setText(getProfileResponse.data?.first_name + " " + getProfileResponse.data?.middle_name + " " + getProfileResponse.data?.last_name)
        textAddress.setText(getProfileResponse.data?.address_1)
        if (getProfileResponse.data?.profile_pic != null) {
            Glide.with(this).load(getProfileResponse.data?.profile_pic)
                .into(imageNavigator)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_edit_profile -> {
                var intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_password -> {
                var intent = Intent(this, UpdatePasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                AlertDialog.onShowLogoutDialog(this, this)
            }
            R.id.nav_cases -> {
                startActivity(Intent(this@HomeActivity, MyCasesActivity::class.java))
            }
            R.id.nav_my_earning -> startActivity(
                Intent(
                    this@HomeActivity,
                    MyEarningsActivity::class.java
                )
            )

            R.id.nav_invite_friends -> {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.google.co.in/")
                shareIntent.type = "text/plain"
                startActivity(Intent.createChooser(shareIntent, "send to"))

            }
            R.id.nav_terms_and_conditions -> startActivity(
                Intent(
                    this@HomeActivity,
                    TermsAndConditionActivity::class.java
                )
            )
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun handleKeyboard(): View {
        return drawerLayout
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (mToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onGetProfileSucess(getProfileResponse: GetProfileResponse) {
        dismissProgress()
        loadNavHeader(getProfileResponse)
        var gson = getProfileResponse.data
        PreferenceHandler.writeString(this, PreferenceHandler.PROFILE_JSON, gson.toString())

        val jsonString = GsonBuilder().create().toJson(getProfileResponse)
        //Save that String in SharedPreferences
        PreferenceHandler.writeString(this, PreferenceHandler.PROFILE_JSON, jsonString)
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.USER_ROLE,
            getProfileResponse.data?.user_role.toString()
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.CONTACT_NUMBER,
            getProfileResponse.data?.username.toString()
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.USER_ID,
            getProfileResponse.data?.id.toString()
        )
    }


    override fun ongetProfileFailure(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onShowError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onClick() {
        finish()
        PreferenceHandler.clearPreferences(this)
        var intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun updateUi(location: Location) {
        Utilities.showMessage(this, "" + location.latitude + " " + location.longitude)
    }

    override fun onLocationNotFound() {
        Utilities.showMessage(this, "location not found")
    }
}