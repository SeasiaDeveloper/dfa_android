package com.dfa.ui.home.fragments.home.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.dfa.R
import com.dfa.adapters.TabLayoutAdapter
import com.dfa.base.BaseActivity
import com.dfa.customviews.CustomtextView
import com.dfa.listeners.AdharNoListener
import com.dfa.pojo.response.*
import com.dfa.ui.contactus.ContactUsActivity
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
import com.dfa.ui.policedetail.view.PoliceIncidentDetailScreen
import com.dfa.ui.profile.ProfileActivity
import com.dfa.ui.updatepassword.view.GetLogoutDialogCallbacks
import com.dfa.ui.updatepassword.view.UpdatePasswordActivity
import com.dfa.utils.*
import com.dfa.utils.alert.AlertDialog
import com.dfa.utils.GpsUtils
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.nav_action.*
import kotlinx.android.synthetic.main.nav_header.*

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
    private var isGPS: Boolean = false
    var isFirstTimeEntry = true

    override fun getLayout(): Int {
        return R.layout.home_activity
    }

    override fun adharNoListener(adhaarNo: String) {
        // check is Adhar no valid
        if (!(Utilities.validateAadharNumber(adhaarNo))) {
            Toast.makeText(this, getString(R.string.adhar_not_valid), Toast.LENGTH_SHORT).show()
        } else {
            Utilities.showProgress(this)
            //hit Api to save the adhar no in backend
            homePresenter.saveAdhaarNo(authorizationToken!!, adhaarNo)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        if (menuItem != null && menuItem!!.isChecked) menuItem!!.isChecked = false

        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")

        if (!authorizationToken!!.isEmpty()) {
            homePresenter.hitProfileApi(authorizationToken)
        }
        if (!isGPS && !isPermissionDialogRequired) {
            askForGPS()
        }

        registerReceiver(
            refreshReceiver,
            IntentFilter("policeJsonReceiver")
        ) //registering the broadcast receiver
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(refreshReceiver) //unregistering the broadcast receiver
    }

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (PreferenceHandler.readString(
                    this@HomeActivity,
                    PreferenceHandler.USER_ROLE,
                    "0"
                ).equals("2")
            ) {
                val notificationResponse =
                    intent.getSerializableExtra("notificationResponse") as NotificationResponse
                displayAcceptRejDialog(notificationResponse)
            }
        }
    }

    fun displayAcceptRejDialog(notificationResponse: NotificationResponse) {
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this@HomeActivity),
                R.layout.layout_accept_reject_alert,
                null,
                false
            )

        val dialog = Dialog(this@HomeActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        (dialog.findViewById(R.id.txtComplainerContact) as TextView).text =
            notificationResponse.username
        (dialog.findViewById(R.id.txtComplaintDate) as TextView).text =
            notificationResponse.report_data
        (dialog.findViewById(R.id.txtComplaintTime) as TextView).text =
            notificationResponse.report_time
        if (!notificationResponse.description.equals("") && notificationResponse.description != null) {
            (dialog.findViewById(R.id.layout_desc) as LinearLayout).visibility = View.VISIBLE
            (dialog.findViewById(R.id.txtDescription) as TextView).text =
                notificationResponse.description
        } else {
            (dialog.findViewById(R.id.layout_desc) as LinearLayout).visibility = View.GONE
        }

        val acceptButton = dialog.findViewById(R.id.btnAccept) as CustomtextView
        val rejectButton = dialog.findViewById(R.id.btnReject) as CustomtextView
        val openButton = dialog.findViewById(R.id.btnOpen) as CustomtextView

        acceptButton.setOnClickListener {
            //accept = 4
            Utilities.showProgress(this)
            //hit status update api for accept status
            homePresenter.updateStatus(
                authorizationToken!!,
                notificationResponse.complaint_id.toString(),
                "4"
            )
            dialog.dismiss()
            try {
                genPubHomeFrag.refreshList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //GeneralPublicHomeFragment.change = 1
        }

        rejectButton.setOnClickListener {
            //reject = 6
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
            val intent = Intent(this, PoliceIncidentDetailScreen::class.java)
            intent.putExtra(
                Constants.PUBLIC_COMPLAINT_DATA,
                notificationResponse.complaint_id.toString()
            )
            intent.putExtra(Constants.POST_OR_COMPLAINT, "0") // 0 is for complaint type
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
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

        getLocation()

        //dialog
        if (getIntent() != null && getIntent().getExtras() != null && (getIntent().getExtras()?.getString(
                "complaint_id"
            ) != null) && getIntent().getExtras()?.getString("report_time") != "" &&
            PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "") == "2"
        ) {
            val notificationResponse = NotificationResponse()
            notificationResponse.username = getIntent().getExtras()?.getString("username")
            notificationResponse.report_time = getIntent().getExtras()?.getString("report_time")
            notificationResponse.report_data = getIntent().getExtras()?.getString("report_data")
            notificationResponse.description = getIntent().getExtras()?.getString("description")
            notificationResponse.complaint_id = getIntent().getExtras()?.getString("complaint_id")
            notificationResponse.is_notify = getIntent().getExtras()?.getString("is_notify")
            displayAcceptRejDialog(notificationResponse)
        }
    }

    @SuppressLint("SetTextI18n")
    fun setTabAdapter() {

        val adapter = TabLayoutAdapter(supportFragmentManager)
        if (!genPubHomeFrag.isAdded) {
            adapter.addFragment(genPubHomeFrag, "Home")
        }
        adapter.addFragment(EmergencyFragment(), "Emergency")
        //adapter.addFragment(CasesFragment(), "Cases")
        adapter.addFragment(PhotosFragment(), "Photos")
        adapter.addFragment(VideosFragment(), "Videos")
        viewPager?.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        nav_view?.setNavigationItemSelectedListener(this)

        var menu = nav_view.menu


        val role = PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "0")
        if (role.equals("0")) {
            userType.setText(getString(R.string.gpu))

        } else if (role.equals("1")) {
            userType.setText(getString(R.string.ngo_user))

        } else if (role.equals("2")) {
            userType.setText(getString(R.string.police_user))

        }



        if (authorizationToken!!.isEmpty()) {
            menu.findItem(R.id.nav_edit_profile).setVisible(false)
            menu.findItem(R.id.nav_password).setVisible(false)
            menu.findItem(R.id.nav_logout).setVisible(false)
            menu.findItem(R.id.nav_cases).setVisible(false)
            userType.setText(getString(R.string.guest_user))
            btnLogin.visibility = View.VISIBLE
        } else {
            menu.findItem(R.id.nav_edit_profile).setVisible(true)
            menu.findItem(R.id.nav_password).setVisible(true)
            menu.findItem(R.id.nav_logout).setVisible(true)
            menu.findItem(R.id.nav_cases).setVisible(true)
            btnLogin.visibility = View.GONE
        }

        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        if (authorizationToken!!.isEmpty()) {
            textName.setText(getString(R.string.guest_user))
        }

    }

    //checking location
    private fun getLocation() {
        if (!Utilities.checkPermissions(this)) {
            Utilities.requestPermissions(this)
        } else {
            //  askForGPS()
            isPermissionDialogRequired = false
        }
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
                Utilities.requestPermissions(this)
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
                if (isGPS) {
                    //location
                    locationCallBack = this@HomeActivity;

                    ForegroundService.startService(
                        this@HomeActivity,
                        "Syncing your location..",
                        locationCallBack
                    )
                    Utilities.showProgress(this@HomeActivity)
                }
            }
        })
    }

    private fun loadNavHeader(getProfileResponse: GetProfileResponse) { // name, wegbsite
        textName.setText(getProfileResponse.data?.first_name + " " + getProfileResponse.data?.middle_name + " " + getProfileResponse.data?.last_name)
        textAddress.setText(getProfileResponse.data?.address_1)
        if (authorizationToken!!.isEmpty()) {
            userInfo.visibility = View.GONE
        } else {
            userInfo.visibility = View.VISIBLE
            /*  if (getProfileResponse.data?.isVerified!!.equals("1")) {
                  userInfo.setText("Verified")
                  verified_icon.visibility = View.VISIBLE
              } else {
                  userInfo.setText("Unverified")
                  verified_icon.visibility = View.GONE
              }
  */
            val role = PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "0")
            if (role.equals("0")) {
                userInfo.setText(getString(R.string.gpu))
                if (getProfileResponse.data?.isVerified!!.equals("1")) {
                    verified_icon.visibility = View.VISIBLE
                } else {
                    verified_icon.visibility = View.GONE
                }
            } else if (role.equals("1")) {
                userInfo.setText(getString(R.string.ngo_user))
                verified_icon.visibility = View.VISIBLE

            } else if (role.equals("2")) {
                userInfo.setText(getString(R.string.police_user))
                verified_icon.visibility = View.VISIBLE
            }

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

        if (getProfileResponse.data?.profile_pic != null) {
            try {
                Glide.with(this).load(getProfileResponse.data.profile_pic)
                    .into(imageNavigator)
            } catch (e: Exception) {
                e.printStackTrace()
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
                startActivity(Intent(this@HomeActivity, MyCasesActivity::class.java))
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

            R.id.nav_invite_friends -> {
                val appUrl = PreferenceHandler.readString(this, PreferenceHandler.APP_URL, "")
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, appUrl)
                shareIntent.type = "text/plain"
                startActivity(Intent.createChooser(shareIntent, "send to"))

            }
            R.id.nav_terms_and_conditions -> Utilities.showMessage(this@HomeActivity, "Coming Soon")
            /*startActivity(
                Intent(
                    this@HomeActivity,
                    TermsAndConditionActivity::class.java
                )
            )*/
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
        ForegroundService.stopService(this)
        finish()
        PreferenceHandler.clearPreferences(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
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
                    ForegroundService.stopService(this)
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
            genPubHomeFrag.refreshList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun adhaarSavedSuccess(responseObject: SignupResponse) {
        Utilities.showMessage(this, responseObject.message)
        val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        jsondata.data?.adhar_number = responseObject.data.adhar_number //add adhar no
        Utilities.dismissProgress()
        val intent = Intent(this, GeneralPublicActivity::class.java)
        startActivity(intent)
    }

}