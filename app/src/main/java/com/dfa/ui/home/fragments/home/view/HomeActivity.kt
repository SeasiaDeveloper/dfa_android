package com.dfa.ui.home.fragments.home.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.telephony.ServiceState
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dfa.R
import com.dfa.adapters.TabLayoutAdapter
import com.dfa.application.GetVersionCode
import com.dfa.application.MyApplication
import com.dfa.base.BaseActivity
import com.dfa.databinding.LayoutAcceptRejectAlertBinding
import com.dfa.listeners.AdharNoListener
import com.dfa.maps.FusedLocationClass
import com.dfa.pojo.response.*
import com.dfa.ui.contactus.ContactUsActivity
import com.dfa.ui.contribute.ContributeActivity
import com.dfa.ui.contribute.TicketResponse
import com.dfa.ui.earnings.view.MyEarningsActivity
import com.dfa.ui.emergency.view.EmergencyFragment
import com.dfa.ui.generalpublic.GeneralPublicActivity
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.home.fragments.cases.view.LocationListenerCallback
import com.dfa.ui.home.fragments.home.presenter.HomePresenter
import com.dfa.ui.home.fragments.home.presenter.HomePresenterImpl
import com.dfa.ui.home.fragments.photos.view.PhotosFragment
import com.dfa.ui.home.fragments.videos.view.VideosFragment
import com.dfa.ui.login.view.LoginActivity
import com.dfa.ui.mycases.MyCasesActivity
import com.dfa.ui.mycases.NodelMyCaseActivity
import com.dfa.ui.policedetail.view.PoliceIncidentDetailScreen
import com.dfa.ui.privacy_policy.PrivacyPolicyActivity
import com.dfa.ui.profile.ProfileActivity
import com.dfa.ui.termsConditions.view.TermsAndConditionActivity
import com.dfa.ui.updatepassword.view.GetLogoutDialogCallbacks
import com.dfa.ui.updatepassword.view.UpdatePasswordActivity
import com.dfa.utils.*
import com.dfa.utils.alert.AlertDialog
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.nav_action.*
import kotlinx.android.synthetic.main.nav_header.*
import java.lang.Double.parseDouble

class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, HomeView,
    GetLogoutDialogCallbacks, LocationListenerCallback, AdharNoListener {

    private var mDrawerLayout: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null
    private var mToolbar: Toolbar? = null
    private var homePresenter: HomePresenter = HomePresenterImpl(this)
    private var authorizationToken: String? = null
    private lateinit var locationCallBack: LocationListenerCallback
    private var isPermissionDialogRequired = true
    var genPubHomeFrag = GeneralPublicHomeFragment()

    //    var genPubHomeFrag = HomeFragment()
    private var isGPS: Boolean = false
    var isFirstTimeEntry = true
    val player: MediaPlayer?=null
    private var provider: String = ""
    var role = ""
    private lateinit var locationManager: LocationManager
    var gps_enabled: Boolean = false
    lateinit var getProfileresponse: GetProfileResponse
    //   lateinit var btnLogin:CustomtextView

    private var mFusedLocationClass: FusedLocationClass? = null
    private var mLocation: Location? = null
    private var mHandler: Handler? = null

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mFusedLocationClass != null) {
                mLocation = mFusedLocationClass?.getLastLocation(this@HomeActivity)
                if (mLocation != null) {
                    val mAddress: String = Utilities.getAddressFromLatLong(

                        mLocation!!.getLatitude(),
                        mLocation!!.getLongitude(),
                        this@HomeActivity
                    )
                    var lattitude = mLocation!!.getLatitude().toString() + ""
                    var longitude = mLocation!!.getLongitude().toString() + ""
                    val TAG = "HomeActivity"
                    PreferenceHandler.writeString(
                        this@HomeActivity,
                        PreferenceHandler.LATITUDE,
                        lattitude
                    )
                    PreferenceHandler.writeString(
                        this@HomeActivity,
                        PreferenceHandler.LONGITUDE,
                        longitude
                    )
//                    Toast.makeText(
//                        this@HomeActivity,
//                        lattitude + "---" + longitude,
//                        Toast.LENGTH_LONG
//                    ).show()
                    mHandler!!.removeCallbacks(this)
                } else mHandler!!.postDelayed(this, 500)
            } else mHandler!!.postDelayed(this, 500)
        }
    }


    override fun getLayout(): Int {
        return R.layout.home_activity
    }

    override fun adharNoListener(adhaarNo: String) {
        // check is Adhar no valid
        if (!(Utilities.validateAadharNumber(adhaarNo))) {
            Toast.makeText(this, getString(R.string.adhar_not_valid), Toast.LENGTH_SHORT).show()
        } else {
            if (isOnline(this)) {
                Utilities.showProgress(this)
                //hit Api to save the adhar no in backend
                homePresenter.saveAdhaarNo(authorizationToken!!, adhaarNo)
            } else {
                Utilities.showMessage(this, getString(R.string.no_internet_connection))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (menuItem != null && menuItem!!.isChecked) menuItem!!.isChecked = false

        if (!isGPS && !isPermissionDialogRequired) {
            askForGPS()
        }

        registerReceiver(
            refreshReceiver,
            IntentFilter("policeJsonReceiver")
        ) //registering the broadcast receiver

        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        if (!authorizationToken!!.isEmpty()) {
            var internetUtils = InternetUtils()
            if (internetUtils.isOnline(this)) {
                homePresenter.hitProfileApi(authorizationToken)
            } else {
                Utilities.showMessage(this, getString(R.string.no_internet_connection))
            }
        }

        if (btnLogin != null) {
            btnLogin.setOnClickListener {
                // ForegroundService.stopService(this)
                finish()
                PreferenceHandler.clearPreferences(this)
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        try {
            if(player!=null){
                if(player.isPlaying){
                    player.stop()
                }
            }
        }catch (e:Exception){

        }

        unregisterReceiver(refreshReceiver) //unregistering the broadcast receiver
    }

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (PreferenceHandler.readString(this@HomeActivity, PreferenceHandler.USER_ROLE, "0")
                    .equals("2") || PreferenceHandler.readString(
                    this@HomeActivity,
                    PreferenceHandler.USER_ROLE,
                    "0"
                ).equals("3")
            ) {
                val notificationResponse =
                    intent.getSerializableExtra("notificationResponse") as NotificationResponse
                displayAcceptRejDialog(notificationResponse)
            }
        }
    }

    fun displayAcceptRejDialog(notificationResponse: NotificationResponse) {
        lateinit var dialog: android.app.AlertDialog




        val builder = android.app.AlertDialog.Builder(this@HomeActivity)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this@HomeActivity),
                R.layout.layout_accept_reject_alert,
                null,
                false
            ) as LayoutAcceptRejectAlertBinding

//    (dialog.findViewById(R.id.txtComplainerContact) as TextView).text =
//        notificationResponse.username


        binding.txtComplaintTime.text =

            Utilities.changeDateFormat(notificationResponse.report_data!!) + " " + Utilities.changeTimeFormat(
                notificationResponse.report_time!!
            )
        binding.txtCrimeType.text =
            notificationResponse.crime_type

        binding.txtUrgencyValue.text =
            notificationResponse.urgency




        if (notificationResponse.latitude == null || notificationResponse.latitude == "0" || notificationResponse.latitude == "" || notificationResponse.latitude == "")
            binding.txtlocationValue.text = "NA"
        else {
            binding.txtlocationValue.text =
                Utilities.getAddressFromLatLong(
                    parseDouble(notificationResponse.latitude + ""),
                    parseDouble(notificationResponse.longitude + ""),
                    this
                )
        }

        val soundUri  = Uri.parse("android.resource://"
                + MyApplication.instance.getPackageName() + "/" + R.raw.siren);

        val player: MediaPlayer = MediaPlayer.create(this, soundUri)
        player.isLooping = true
        player.start()


        val acceptButton = binding.btnAccept
        val rejectButton = binding.btnReject
        val openButton = binding.btnOpen

        acceptButton.setOnClickListener {
            //accept = 4
            if(player.isPlaying){
                player.stop()
            }
            Utilities.showProgress(this)
            //hit status update api for accept status
            homePresenter.updateStatus(
                authorizationToken!!,
                notificationResponse.complaint_id.toString(),
                "4"
            )
            dialog.dismiss()
            try {
                //genPubHomeFrag.refreshList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //GeneralPublicHomeFragment.change = 1
        }

        rejectButton.setOnClickListener {
            //reject = 6
            player.stop()
            Utilities.showProgress(this)
            //hit status update api for reject status
            homePresenter.updateStatus(
                authorizationToken!!,
                notificationResponse.complaint_id.toString(),
                "6"
            )
            dialog.dismiss()
        }

        openButton.setOnClickListener {
            if(player.isPlaying){
                player.stop()
            }
            val intent = Intent(this, PoliceIncidentDetailScreen::class.java)
            intent.putExtra(
                Constants.PUBLIC_COMPLAINT_DATA,
                notificationResponse.complaint_id.toString()
            )

            intent.putExtra(Constants.POST_OR_COMPLAINT, "0") // 0 is for complaint type
            startActivity(intent)
            dialog.dismiss()
        }




        builder.setView(binding.root)
        dialog = builder.create()
        dialog.show()

        dialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {

                if(player.isPlaying){
                    player.stop()
                }
            }
        })



    }

    override fun setupUI() {
        mToolbar = findViewById<View>(R.id.nav_action) as Toolbar
        setSupportActionBar(mToolbar)
        mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout!!.addDrawerListener(mToggle!!)
        mToggle!!.syncState()



        getSupportActionBar()?.setHomeButtonEnabled(true)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.burger_icon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        val role = PreferenceHandler.readString(this@HomeActivity, PreferenceHandler.USER_ROLE, "0")

        if (role.equals("0"))
            toolbar_title.text = getString(R.string.public_dashboard)
        else if (role.equals("1"))
            toolbar_title.text = getString(R.string.ngo_dashboard)
        else if (role.equals("2"))
            toolbar_title.text = getString(R.string.police_dashboard)
        (nav_action as Toolbar).setTitleTextColor(Color.BLACK)

        getStartingLocation()

        //dialog
        if (getIntent() != null && getIntent().getExtras() != null && (getIntent().getExtras()
                ?.getString("complaint_id") != null) && getIntent().getExtras()
                ?.getString("report_time") != "" && (PreferenceHandler.readString(
                this,
                PreferenceHandler.USER_ROLE,
                ""
            ) == "2" || PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "") == "3")
        ) {
            val notificationResponse = NotificationResponse()
            notificationResponse.username = getIntent().getExtras()?.getString("username")
            notificationResponse.report_time = getIntent().getExtras()?.getString("report_time")
            notificationResponse.report_data = getIntent().getExtras()?.getString("report_data")
            notificationResponse.description = getIntent().getExtras()?.getString("description")
            notificationResponse.complaint_id = getIntent().getExtras()?.getString("complaint_id")
            notificationResponse.is_notify = getIntent().getExtras()?.getString("is_notify")
            notificationResponse.urgency = getIntent().getExtras()?.getString("urgency")
            notificationResponse.latitude = getIntent().getExtras()?.getString("latitude")
            notificationResponse.longitude = getIntent().getExtras()?.getString("longitude")
            notificationResponse.crime_type = getIntent().getExtras()?.getString("crime_type")

            displayAcceptRejDialog(notificationResponse)
        }

        setTabAdapter()
        mFusedLocationClass = FusedLocationClass(this)
        mHandler = Handler()
        mHandler!!.postDelayed(mRunnable, 500)
        GetVersionCode(this).execute()

//        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//           override fun onTabSelected(tab: TabLayout.Tab?) {
//               if(tabs.selectedTabPosition.toString().equals("0")){
//                   btnSwip.visibility=View.VISIBLE
//               } else{
//                   btnSwip.visibility=View.GONE
//               }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {}
//            override fun onTabReselected(tab: TabLayout.Tab?) {}
//        })


    }

    @SuppressLint("SetTextI18n")
    fun setTabAdapter() {

        val adapter = TabLayoutAdapter(supportFragmentManager)
        if (!genPubHomeFrag.isAdded) {
            adapter.addFragment(genPubHomeFrag, "Home")

        }
        //adapter.addFragment(genPubHomeFrag, "Home")
        adapter.addFragment(EmergencyFragment(), "Emergency")
        adapter.addFragment(PhotosFragment(), "Photos")
        adapter.addFragment(VideosFragment(), "Videos")
//        adapter.addFragment(MarketPlaceFragment(), "Market Place")
        viewPager?.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        nav_view?.setNavigationItemSelectedListener(this)


        var menu = nav_view.menu

        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        if (authorizationToken!!.isEmpty()) {
            menu.findItem(R.id.nav_edit_profile).setVisible(false)
            menu.findItem(R.id.nav_password).setVisible(false)
            menu.findItem(R.id.nav_logout).setVisible(false)
            menu.findItem(R.id.nav_cases).setVisible(false)
            menu.findItem(R.id.nav_contribute_guest).setVisible(true)
            menu.findItem(R.id.nav_contribute).setVisible(false)
            //userType.setText(getString(R.string.guest_user))
        } else {
            menu.findItem(R.id.nav_edit_profile).setVisible(true)
            menu.findItem(R.id.nav_password).setVisible(true)
            menu.findItem(R.id.nav_logout).setVisible(true)
            menu.findItem(R.id.nav_cases).setVisible(true)
            menu.findItem(R.id.nav_contribute_guest).setVisible(false)
            menu.findItem(R.id.nav_contribute).setVisible(true)
        }
        homePresenter.dueAmountInput(authorizationToken!!)
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerview = navigationView.getHeaderView(0)
        val profilename =
            headerview.findViewById<View>(R.id.btnLogin) as TextView

        if (profilename != null) {
            profilename.setOnClickListener {
                // ForegroundService.stopService(this)

                PreferenceHandler.clearPreferences(this)
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getLocation() {
        if (!Utilities.checkPermissions(this))
            Utilities.requestPermissions(this)

    }

    //checking location
    private fun getStartingLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        provider = LocationManager.GPS_PROVIDER

        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
              try {
                  gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
              } catch (ex: Exception) {
              }
              try {
                  network_enabled =
                      locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
              } catch (ex: Exception) {
              }
              if (!gps_enabled && !network_enabled) {
                  askForGPS()
              }

          } else {*/
        if (!Utilities.checkPermissions(this)) {
            Utilities.requestPermissions(this)
        } else {
            //  askForGPS()
            isPermissionDialogRequired = false
        }
        //}
    }

    //handling callback of Location permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == Utilities.PERMISSION_ID) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED) || (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                //  askForGPS()
                isPermissionDialogRequired = false
            } else {
                AlertDialog.settingDialog(this)
            }
        }
    }

    //checking GPS
    private fun askForGPS() {
        GpsUtils(this)
            .turnGPSOn(object : GpsUtils.onGpsListener {
                override fun gpsStatus(isGPSEnable: Boolean) {
                    // turn on GPS
                    isGPS = isGPSEnable
                    if (isGPS)
                        getLocation()
                }
            })
    }

    private fun loadNavHeader(getProfileResponse: GetProfileResponse) { // name, wegbsite
        if (getProfileResponse.data != null) {

            var data = getProfileResponse.data
            var middleName = ""
            if (data.middle_name.toString() != "" && data.middle_name.toString() != "null")
                middleName = data.middle_name.toString()

            textName.setText(getProfileResponse.data?.first_name + " " + middleName + " " + getProfileResponse.data?.last_name)
        }
        if (authorizationToken!!.isEmpty()) {
            userInfo.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
        } else {
            btnLogin.visibility = View.GONE
            role = PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "0")!!
            if (role.equals("0")) {
                // userInfo.setText(getString(R.string.gpu))
                if (getProfileResponse.data?.isVerified!!.equals("1")) {
                    if (!role.equals("0")) {
                        verified_icon.visibility = View.VISIBLE
                    }
                } else {
                    verified_icon.visibility = View.GONE
                }
                textAddress.visibility = View.GONE
            } else if (role.equals("1")) {
                userInfo.visibility = View.VISIBLE
                userInfo.setText(getString(R.string.ngo_user))
                verified_icon.visibility = View.VISIBLE
                textAddress.visibility = View.VISIBLE
            } else if (role.equals("2")) {
                userInfo.visibility = View.VISIBLE
                userInfo.setText(getString(R.string.police_user))
                verified_icon.visibility = View.VISIBLE
                textAddress.visibility = View.VISIBLE
            } else if (role.equals("3")) {
                userInfo.visibility = View.VISIBLE
                userInfo.setText(getString(R.string.nodel_officer_user))
                verified_icon.visibility = View.VISIBLE
                textAddress.visibility = View.VISIBLE
            }



            textAddress.setText(getProfileResponse.data?.address_1)

        }

        userInfo.setOnClickListener {
            if (!authorizationToken!!.isEmpty()) {
                val value =
                    PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
                val jsondata =
                    GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
                if (jsondata != null) {
                    //check if user is partial/fully verified
                    if (jsondata.data?.adhar_number != null && !(jsondata.data.adhar_number.equals(
                            ""
                        ))
                    ) {
                        /* val intent = Intent(this, GeneralPublicActivity::class.java)
                         startActivity(intent)*/
                    } else {
                        drawerLayout.closeDrawer(GravityCompat.START)
                        //make the user partially verified:
                        Utilities.displayInputDialog(this, this)
                    }
                }
            } else {
                com.dfa.utils.alert.AlertDialog.guesDialog(this)
            }
        }

        val options1 = RequestOptions()
            /* .centerCrop()*/
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.user)
            .error(R.drawable.user)
        if (getProfileResponse.data != null) {
            if (getProfileResponse.data?.profile_pic != null) {
                try {
                    Glide.with(this).load(getProfileResponse.data.profile_pic).apply(options1)
                        .into(imageNavigator)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    Glide.with(this).load(R.drawable.user).apply(options1)
                        .into(imageNavigator)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    var menuItem: MenuItem? = null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        menuItem = item

        when (item.itemId) {

            R.id.nav_edit_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("fromWhere", "editProfile")
                startActivity(intent)
            }
            R.id.nav_password -> {
                val intent = Intent(this, UpdatePasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                AlertDialog.onShowLogoutDialog(this, this)
            }
            R.id.nav_cases -> {

                if (role.equals("3")) {
                    startActivity(Intent(this@HomeActivity, NodelMyCaseActivity::class.java))

                } else {
                    startActivity(Intent(this@HomeActivity, MyCasesActivity::class.java))
                }
            }
            R.id.nav_my_earning ->
                if (!authorizationToken!!.isEmpty()) {

                    startActivity(Intent(this@HomeActivity, MyEarningsActivity::class.java))

                } else {
                    com.dfa.utils.alert.AlertDialog.guesDialog(this)
                }


            R.id.nav_contact_us -> {
                startActivity(Intent(this@HomeActivity, ContactUsActivity::class.java))
            }

            R.id.nav_contribute -> {
                startActivity(Intent(this@HomeActivity, ContributeActivity::class.java))
            }
            R.id.nav_contribute_guest -> {
                startActivity(Intent(this@HomeActivity, ContributeActivity::class.java))
            }

            R.id.nav_invite_friends -> {
                if (authorizationToken!!.isEmpty()) {
                    PreferenceHandler.writeString(
                        this,
                        PreferenceHandler.APP_URL,
                        "https://play.google.com/store/apps/details?id=com.dfango.android&hl=en"
                    )
//                    PreferenceHandler.writeString(
//                        this,\
//                        PreferenceHandler.APP_URL,
//                        "https://play.google.com/store/apps/details?id=com.moonwalk.app&hl=en"
//                    )
                }
                val appUrl = PreferenceHandler.readString(this, PreferenceHandler.APP_URL, "")
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.dfango.android&hl=en")
                shareIntent.type = "text/plain"
                startActivity(Intent.createChooser(shareIntent, "send to"))

            }
            R.id.nav_terms_and_conditions ->
                startActivity(
                    Intent(
                        this@HomeActivity,
                        TermsAndConditionActivity::class.java
                    )
                )
            R.id.privacy_policy ->
                startActivity(
                    Intent(
                        this@HomeActivity,
                        PrivacyPolicyActivity::class.java
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
        mToggle
        return if (mToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onGetProfileSucess(getProfileResponse: GetProfileResponse) {
        dismissProgress()
        loadNavHeader(getProfileResponse)
        val gson = getProfileResponse.data
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.USER_FULLNAME,
            gson?.first_name + " " + gson?.last_name
        )
        PreferenceHandler.writeString(this, PreferenceHandler.PROFILE_JSON, gson.toString())
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.APP_URL,
            getProfileResponse.data?.app_url!!
        )

        //save NGO details for ContactUs screen
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.NGO_CONTACT_NO,
            getProfileResponse.data.ngo_phone!!
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.NGO_NAME,
            getProfileResponse.data.ngo_name!!
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.NGO_ADDRESS,
            getProfileResponse.data.ngo_address!!
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.NGO_DIST,
            getProfileResponse.data.ngo_dist!!
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.NGO_STATE,
            getProfileResponse.data.ngo_state!!
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.NGO_PIN,
            getProfileResponse.data.ngo_pincode!!
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.NGO_LONGITUDE,
            getProfileResponse.data.ngo_longitude!!
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.NGO_LATITUDE,
            getProfileResponse.data.ngo_latitude!!
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.NGO_EMAIL,
            getProfileResponse.data.ngo_email!!
        )

        val jsonString = GsonBuilder().create().toJson(getProfileResponse)
        //Save that String in SharedPreferences
        PreferenceHandler.writeString(this, PreferenceHandler.PROFILE_JSON, jsonString)

        val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)

        PreferenceHandler.writeString(
            this,
            PreferenceHandler.CONTACT_NUMBER,
            getProfileResponse.data.username.toString()
        )
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.USER_ID,
            getProfileResponse.data?.id.toString()
        )

        navigationLayout.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("fromWhere", "editProfile")
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(intent)
        }
    }

    override fun ongetProfileFailure(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onShowError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onClick() {

        val authorizationToken =
            PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")

        if (InternetUtils().isOnline(this)) {
            Utilities.showProgress(this)
            homePresenter.logout(authorizationToken!!)

        } else {
            Utilities.showMessage(
                this,
                getString(R.string.no_internet_connection)
            )
        }

    }

    override fun onPostLocationSucess(postLocationResponse: PostLocationResponse) {
        // Utilities.showMessage(applicationContext, "sucess")
    }

    override fun onPostLocationFailure(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(applicationContext, "failuree")
    }

    override fun updateUi(location: Location) {
        if (isFirstTimeEntry) {
            try {

                setTabAdapter()
                btnLogin.setOnClickListener {
                    // ForegroundService.stopService(this)
                    finish()
                    PreferenceHandler.clearPreferences(this)
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

            } catch (e: Exception) {
                Log.e("Tab Adapter", "Exception----->" + e)
            }
            isFirstTimeEntry = false
        }

        PreferenceHandler.writeString(
            this,
            PreferenceHandler.LATITUDE,
            "" + location.latitude
        )

        PreferenceHandler.writeString(
            this,
            PreferenceHandler.LONGITUDE,
            "" + location.longitude
        )
        homePresenter.hitLocationApi(
            authorizationToken,
            location.latitude.toString(),
            location.longitude.toString()
        )
    }

    override fun onLocationNotFound() {
    }

    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, responseObject.message.toString())
        try {
//            genPubHomeFrag.refreshList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun adhaarSavedSuccess(responseObject: SignupResponse) {
        // Utilities.showMessage(this, responseObject.message)
        Utilities.showMessage(this, "Aadhaar card added successfully")

        val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        jsondata.data?.adhar_number = responseObject.data.adhar_number //add adhar no
        Utilities.dismissProgress()
        val intent = Intent(this, GeneralPublicActivity::class.java)
        startActivity(intent)
    }

    override fun onLogoutSuccess(responseObject: CommonResponse) {
        // ForegroundService.stopService(this)
        Utilities.showMessage(this, responseObject.message)
        PreferenceHandler.clearPreferences(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()

    }

    override fun dueAmountSuccess(responseObject: DueTicketResponse) {

        var dueTicket=ArrayList<DueTicketResponse.Due_tickets>()
        dueTicket=responseObject.due_tickets!!
        if(dueTicket.size>0){
            dueIncomePopup(dueTicket)
        }

        //    Toast.makeText(this,"hit api",Toast.LENGTH_LONG).show()
    }

    fun dueIncomePopup(dueTicket: ArrayList<DueTicketResponse.Due_tickets>) {
        var dialog = Dialog(this!!) // Context, this, etc.
        dialog!!.setContentView(R.layout.due_amount_dialog)

       var  coupanList=ArrayList<TicketResponse.Data>()

        for (i in 0..dueTicket.size-1){
            var inputModel=TicketResponse.Data()
            inputModel.BucketId=dueTicket.get(i).BucketId
            inputModel.Ticket=dueTicket.get(i).Ticket
            coupanList!!.add(inputModel)
        }


        var btnOk = dialog.findViewById<Button>(R.id.btnOk)
        btnOk.setOnClickListener {
            dialog!!.dismiss()
//
            var intent=Intent(this,ContributeActivity::class.java)
            intent.putExtra("dueAmount","dueAmount")
            intent.putExtra("dueTicketList",coupanList)
            startActivity(intent)
        }


        dialog!!.show()
    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val n = cm.activeNetwork
            if (n != null) {
                val nc = cm.getNetworkCapabilities(n)
                //It will check for both wifi and cellular network
                return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            }
            return false
        } else {
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }


}