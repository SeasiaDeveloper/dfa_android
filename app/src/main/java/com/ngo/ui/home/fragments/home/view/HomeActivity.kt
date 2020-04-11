package com.ngo.ui.home.fragments.home.view


import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.view.*
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
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.ngo.R
import com.ngo.adapters.TabLayoutAdapter
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.customviews.CustomtextView
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetProfileResponse
import com.ngo.pojo.response.NotificationResponse
import com.ngo.pojo.response.PostLocationResponse
import com.ngo.ui.policedetail.view.PoliceIncidentDetailScreen
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
import com.ngo.utils.ForegroundService
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import com.ngo.utils.alert.AlertDialog
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.nav_header.*
import com.ngo.utils.*
import kotlinx.android.synthetic.main.nav_action.*
import kotlinx.android.synthetic.main.nav_action.view.*

class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, HomeView,
    GetLogoutDialogCallbacks, LocationListenerCallback {


    private var mDrawerLayout: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null
    private var mToolbar: Toolbar? = null
    private var homePresenter: HomePresenter = HomePresenterImpl(this)
    private var authorizationToken: String? = null
    private lateinit var locationCallBack: LocationListenerCallback
    private var imageUrl: String = ""
    private var isPermissionDialogRequired = true

    private var isGPS: Boolean = false

    override fun getLayout(): Int {
        return R.layout.home_activity
    }

    override fun onResume() {
        super.onResume()
        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        homePresenter.hitProfileApi(authorizationToken)
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
        //  ForegroundService.stopService(applicationContext)
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
        (dialog.findViewById(R.id.txtDescription) as TextView).text =
            notificationResponse.description

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
            //  intent.putExtra(Constants.FROM_WHERE, "nottohit")
            intent.putExtra(Constants.POST_OR_COMPLAINT, "0") //) is for complaint type
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

    fun setTabAdapter() {
        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.addFragment(GeneralPublicHomeFragment(), "Home")
        adapter.addFragment(CasesFragment(), "Cases")
        adapter.addFragment(PhotosFragment(), "Photos")
        adapter.addFragment(VideosFragment(), "Videos")
        viewPager?.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        nav_view?.setNavigationItemSelectedListener(this)
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
        GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
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
                    /*  try {
                          setTabAdapter()
                      } catch (e: Exception) {
                          Log.e("Tab Adapter", "Exception----->" + e)
                      }*/
                }
            }
        })
    }

    private fun loadNavHeader(getProfileResponse: GetProfileResponse) { // name, wegbsite
        textName.setText(getProfileResponse.data?.first_name + " " + getProfileResponse.data?.middle_name + " " + getProfileResponse.data?.last_name)
        textAddress.setText(getProfileResponse.data?.address_1)
        if (getProfileResponse.data?.profile_pic != null) {
            Glide.with(this).load(getProfileResponse.data.profile_pic)
                .into(imageNavigator)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_edit_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
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
        val gson = getProfileResponse.data

        PreferenceHandler.writeString(this, PreferenceHandler.PROFILE_JSON, gson.toString())

        val jsonString = GsonBuilder().create().toJson(getProfileResponse)
        //Save that String in SharedPreferences
        PreferenceHandler.writeString(this, PreferenceHandler.PROFILE_JSON, jsonString)

        val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)

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

        navigationLayout.setOnClickListener {
            var intent = Intent(this, ProfileActivity::class.java)
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
        Utilities.showMessage(applicationContext, "failuree");
    }

    var isFirstTimeEntry = true
    override fun updateUi(location: Location) {
        if (isFirstTimeEntry) {
            try {

                setTabAdapter()

            } catch (e: Exception) {
                Log.e("Tab Adapter", "Exception----->" + e)
            }
            isFirstTimeEntry = false
        }

        //  Utilities.showMessage(applicationContext, "lat lng" + location.latitude);

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

    override fun statusUpdationSuccess(responseObject: DeleteComplaintResponse) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, responseObject.message.toString())
    }

}