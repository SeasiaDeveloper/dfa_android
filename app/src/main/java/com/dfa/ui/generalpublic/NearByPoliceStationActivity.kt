package com.dfa.ui.generalpublic

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.databinding.ActivityNearByPoliceStationBinding
import com.dfa.maps.FusedLocationClass
import com.dfa.pojo.request.ComplaintRequest
import com.dfa.pojo.response.ComplaintResponse
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.GetCrimeTypesResponse
import com.dfa.pojo.response.PStationsListResponse
import com.dfa.ui.emergency.view.EmergencyFragment
import com.dfa.ui.generalpublic.presenter.GetReportCrimeAlertDialog
import com.dfa.ui.generalpublic.presenter.PublicComplaintPresenter
import com.dfa.ui.generalpublic.presenter.PublicComplaintPresenterImpl
import com.dfa.ui.generalpublic.presenter.publicComplaintPresenterNearBy
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.generalpublic.view.PublicComplaintView
import com.dfa.ui.home.fragments.home.view.HomeActivity
import com.dfa.ui.login.view.LoginActivity
import com.dfa.utils.Constants
import com.dfa.utils.GpsUtils
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import com.vincent.videocompressor.VideoCompress
import kotlinx.android.synthetic.main.activity_near_by_police_station.*
import kotlinx.android.synthetic.main.activity_near_by_police_station.publicParentLayout
import kotlinx.android.synthetic.main.activity_public.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NearByPoliceStationActivity : BaseActivity(), View.OnClickListener, PublicComplaintView,
    GetReportCrimeAlertDialog {
    var binding:ActivityNearByPoliceStationBinding?=null
    var etDescription=""
    private var mFusedLocationClass: FusedLocationClass? = null
    private var isPermissionDialogRequired = true
    var  pathOfImages = ArrayList<String>()
    private var complaintsPresenter: PublicComplaintPresenter = publicComplaintPresenterNearBy(this)
    private var mHandler: Handler? = null
    private var mLocation: Location? = null
    private var longitude: String = ""
    private var id: String = ""
    private var changeMedia=0
    private var lattitude: String = ""
    private var isGPS: Boolean = false
    private lateinit var locationManager: LocationManager
    var pstationResponse: PStationsListResponse? = null
    var police_id = ""
    private var authorizationToken: String? = ""
    private var address: String = ""
    val REQUEST_PERMISSIONS = 1
    var range=1
    var mediaType=""
    private val REQUEST_PERMISSIONS_GALLERY_VIDEO = 2
     var getCrimeTypesResponse: GetCrimeTypesResponse?=null
    private var districtList = ArrayList<DistResponse>()
    override fun getLayout(): Int {
       return R.layout.activity_near_by_police_station
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mFusedLocationClass != null) {
                mLocation = mFusedLocationClass?.getLastLocation(this@NearByPoliceStationActivity)
                if (mLocation != null) {
                    val mAddress: String = Utilities.getAddressFromLatLong(

                        mLocation!!.getLatitude(),
                        mLocation!!.getLongitude(),
                        this@NearByPoliceStationActivity
                    )
                    lattitude = mLocation!!.getLatitude().toString() + ""
                    longitude = mLocation!!.getLongitude().toString() + ""
//                    Toast.makeText(
//                       this@GeneralPublicActivity,
//                        lattitude + "---" + longitude,
//                        Toast.LENGTH_LONG
//                    ).show()
                    val TAG = "HomeActivity"
                    Log.d(TAG, "get_current_address: $mAddress")
                    lattitude = mLocation!!.getLatitude().toString() + ""
                    longitude = mLocation!!.getLongitude().toString() + ""
                    mHandler!!.removeCallbacks(this)
                } else mHandler!!.postDelayed(this, 500)
            } else mHandler!!.postDelayed(this, 500)
        }
    }

    override fun setupUI() {
        binding=viewDataBinding as ActivityNearByPoliceStationBinding
        (binding!!.toolbarLayout as CenteredToolbar).title = getString(R.string.police_station)
        (binding!!.toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }


        etDescription=intent.getStringExtra("etDescription")
        id=intent.getStringExtra("id")
        range=intent.getStringExtra("range").toInt()
        changeMedia=intent.getStringExtra("changeMedia").toInt()
        mediaType=intent.getStringExtra("mediaType")
        pathOfImages=intent.extras!!.getStringArrayList("pathOfImages") as ArrayList<String>
        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")

        binding!!.btnSubmit.setOnClickListener(this)
        binding!!.btnSubmitParticular.setOnClickListener(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!Utilities.checkPermissions(this)) {
            Utilities.requestPermissions(this)
        } else {
            //  askForGPS()
            isPermissionDialogRequired = false
        }

        GeneralPublicHomeFragment.changeThroughIncidentScreen = 0
        mFusedLocationClass = FusedLocationClass(this)
        mHandler = Handler()
        mHandler!!.postDelayed(mRunnable, 500)
        if (isInternetAvailable()) {
            showProgress()
            complaintsPresenter.onGetCrimeTypes(authorizationToken)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }


    }

    override fun handleKeyboard(): View {
       return publicParentLayout
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btnSubmit->{
                 police_id = ""
                complaintsPresenter.checkValidations(1, pathOfImages,etDescription)
            }
            R.id.btnSubmitParticular->{
                binding!!.spDistrict.visibility = View.VISIBLE
                getDistrictList()
//               scroll_view.post(Runnable { scroll_view.fullScroll(View.FOCUS_DOWN) })
//                getDistrictList()
            }

        }
    }

    override fun showComplaintsResponse(complaintsResponse: ComplaintResponse) {
        dismissProgress()
        Utilities.showMessage(this, complaintsResponse.message!!)
        //GeneralPublicHomeFragment.change = 1
        GeneralPublicHomeFragment.changeThroughIncidentScreen = 1
       // finish()

        var intent= Intent(this@NearByPoliceStationActivity , HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent)
        finish()

        val fdelete = File(pathOfImages.get(0))
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + pathOfImages.get(0))
                val scanIntent =
                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                scanIntent.data = Uri.fromFile(

                    Environment.getExternalStorageDirectory()
                )
                sendBroadcast(scanIntent)

            } else {
                System.out.println("file not Deleted :" + pathOfImages.get(0))
            }
        }
    }

    override fun showEmptyImageError() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.please_select_media))
    }

    override fun onValidationSuccess() {
        dismissProgress()



        if (police_id != "") {
            lattitude = "0"
            longitude = "0"
            com.dfa.utils.alert.AlertDialog.reportCrimeAlertDialog(this, this)
        } else {

            if (lattitude.equals("") || longitude.equals("")) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                if (lattitude.equals("")) {
                    askForGPS()

                    val handler = Handler()
                    handler.postDelayed(
                        {
                            //var location = locationManager.getLastKnownLocation(provider);
                            mLocation =
                                mFusedLocationClass?.getLastLocation(this@NearByPoliceStationActivity)

                            // Initialize the location fields
                            if (mLocation != null) {
                                //System.out.println("Provider " + provider + " has been selected.");
                                val latti = mLocation!!.latitude
                                val longi = mLocation!!.longitude
                                lattitude = (latti).toString()
                                longitude = (longi).toString()
                                PreferenceHandler.writeString(
                                    this@NearByPoliceStationActivity,
                                    PreferenceHandler.LAT,
                                    lattitude
                                )
                                PreferenceHandler.writeString(
                                    this@NearByPoliceStationActivity,
                                    PreferenceHandler.LNG,
                                    longitude
                                )
                                address = Utilities.getAddressFromLatLong(
                                    lattitude.toDouble(),
                                    longitude.toDouble(),
                                    this@NearByPoliceStationActivity
                                )


                                com.dfa.utils.alert.AlertDialog.reportCrimeAlertDialog(this, this)

                            } else Utilities.showMessage(
                                this,
                                "Unable to get Location, Please try again"
                            )

                        },
                        3000
                    )


                }


            } else com.dfa.utils.alert.AlertDialog.reportCrimeAlertDialog(this, this)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            Utilities.PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                else
                    if (!isGPS) {
                        askForGPS()
                    }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (!isGPS && !isPermissionDialogRequired) {
            askForGPS()
        }
    }
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

    private fun getLocation() {
        if (!checkPermissions(this))
            Utilities.requestPermissions(this)
        else
            try {
                mHandler!!.postDelayed(mRunnable, 500)

                // Request location updates
//                locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER,
//                    0L,
//                    0f,
//                    mLocationListener
//                )
//
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0L,0f,mLocationListener);
//


            } catch (ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available")
            }
    }

    fun checkPermissions(context: Activity): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else false
    }



    override fun showEmptyLevelError() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.select_urgency_level))
    }

    override fun showEmptyDescError() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.please_enter_description))
    }

    override fun getCrimeTypesListSuccess(getCrimeTypesResponse: GetCrimeTypesResponse) {
        dismissProgress()
        this.getCrimeTypesResponse = getCrimeTypesResponse
        val distValueList = ArrayList<String>()
        for (dist in getCrimeTypesResponse.data!!) {
            distValueList.add(dist.name!!)
        }
//        val distArray = distValueList.toArray(arrayOfNulls<String>(distValueList.size))
//        val adapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_spinner_item, distArray
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spTypesOfCrime.setAdapter(adapter)
    }

    override fun getCrimeTyepLstFailure(error: String) {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.crime_types_error))
    }

    override fun getDistrictsSuccess(response: DistResponse) {
        Utilities.dismissProgress()
        districtList.add(response)
        getDistrictDropDown(response)
    }
    fun getDistrictDropDown(response: DistResponse) {
        EmergencyFragment.staticDistValueList = response

        val distValueList = ArrayList<String>()
        binding!!.spPolice.visibility = View.GONE
        distValueList.add("Please select district")

        for (i in 0..response.data.size - 1) {
            distValueList.add(response.data.get(i).name)
        }

        // var list_of_items = arrayOf(distValueList)
        // val distArray = distValueList.toArray(arrayOfNulls<String>(distValueList.size))
        val adapter = ArrayAdapter(
            this,
            R.layout.view_spinner_item, distValueList
        )

        adapter.setDropDownViewResource(R.layout.view_spinner_item)
        binding!!.spDistrict.setAdapter(adapter)

        try {
            binding!!.spDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Display the selected item text on text view
                    "Spinner selected : ${parent.getItemAtPosition(position)}"
                    if (position != 0) {

                        if (isInternetAvailable()) {
                            showProgress()
                            complaintsPresenter.hitpstationApi(response.data.get(position - 1).id)
                        } else {
                            Utilities.showMessage(
                                this@NearByPoliceStationActivity,
                                getString(R.string.no_internet_connection)
                            )
                        }
//                    } else {
//                        var mList: ArrayList<EmergencyDataResponse.Data> = ArrayList()
//                        emergencyDetailsAdapter.changeList(mList)
//                    }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    private fun getDistrictList() {

        if (districtList.size == 0) {
            Utilities.showProgress(this)
            complaintsPresenter.hitDistricApi()
        } else getDistrictDropDown(districtList[0])
    }


    override fun getpStationSuccess(response: PStationsListResponse) {
        dismissProgress()
        //districtList.add(response)
        getStationDropDown(response)
    }

    override fun showServerError(error: String) {
        dismissProgress()
        if (error.equals(Constants.TOKEN_ERROR)) {
            //ForegroundService.stopService(this@GeneralPublicActivity)
            finish()
            PreferenceHandler.clearPreferences(this@NearByPoliceStationActivity)
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            Utilities.showMessage(this, error)
        }
    }

    override fun getCallback() {
//        var id: String? = null
//        if (getCrimeTypesResponse != null) {
//            id = getCrimeTypesResponse!!.data?.get(spTypesOfCrime.selectedItemPosition)?.id
//        }
        val array = arrayOfNulls<String>(pathOfImages.size)



        if (police_id == "" && lattitude == "") {
            Utilities.showMessage(this, "Unable to fetch Location, Please try again")

        } else {

            if (isInternetAvailable()) {
                showProgress()
                val request = ComplaintRequest(
                    id!!, //crimeType
                    range,
                    pathOfImages.toArray(array),
                    etDescription.toString().trim(),
                    lattitude,
                    longitude,
                    mediaType!!,
                    address,
                    police_id
                )
                complaintsPresenter.saveDetailsRequest(
                    authorizationToken,
                    request,
                    this@NearByPoliceStationActivity
                )
            } else {
                Utilities.showMessage(this, getString(R.string.no_internet_connection))
            }
        }
    }
    fun getStationDropDown(response: PStationsListResponse) {
        val distValueList = ArrayList<String>()
        pstationResponse = response

        if (response.data?.size == 0) {
            binding!!.spPolice.visibility = View.GONE
            Utilities.showMessage(this, "No police station available in selected district")
        } else {
            binding!!.spPolice.visibility = View.VISIBLE
           // scroll_view.post(Runnable { scroll_view.fullScroll(View.FOCUS_DOWN) })

        }

        distValueList.add("Please select police station")

        for (i in 0..response.data?.size!! - 1) {
            distValueList.add(response.data?.get(i)?.name.toString())
        }

        // var list_of_items = arrayOf(distValueList)
        // val distArray = distValueList.toArray(arrayOfNulls<String>(distValueList.size))
        val adapter = ArrayAdapter(
            this,
            R.layout.view_spinner_item, distValueList
        )

        adapter.setDropDownViewResource(R.layout.view_spinner_item)
        binding!!.spPolice.setAdapter(adapter)

        try {


            binding!!.spPolice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Display the selected item text on text view
                    "Spinner selected : ${parent.getItemAtPosition(position)}"
                    if (position != 0) {
                        police_id = pstationResponse!!.data?.get(position - 1)?.id.toString()

                        if (etDescription.toString().trim() == "") {
                            Utilities.showMessage(
                                this@NearByPoliceStationActivity,
                                "Please enter description"
                            )

                        } else {
                            if (mediaType.equals("videos")) {
                                if (pathOfImages.size > 0) {
                                    if (!pathOfImages.get(0).isEmpty()
                                    ) {

                                        if (File(pathOfImages.get(0)).length() > 5000) {
                                            videoCompressorCustom(pathOfImages)
                                        } else {
                                            complaintsPresenter.checkValidations(
                                                1,
                                                pathOfImages,
                                                etDescription.toString()
                                            )
                                        }
                                    }
                                } else {
                                    complaintsPresenter.checkValidations(
                                        1,
                                        pathOfImages,
                                        etDescription.toString()
                                    )
                                }
                            } else {
                                complaintsPresenter.checkValidations(
                                    1,
                                    pathOfImages,
                                    etDescription.toString()
                                )
                            }
                        }

                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    private fun videoCompressorCustom(video: ArrayList<String>) {
        if (!video.get(0).isEmpty() && File(video.get(0)).length() > 0) {
            var myDirectory = File(Environment.getExternalStorageDirectory(), "Pictures");

            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }

            var outPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + File.separator + "VID_" + SimpleDateFormat(
                    "yyyyMMdd",
                    getLocale()
                ).format(Date()) + ".mp4";


            var progressDialog = ProgressDialog(this)

            var lengthBeforeCom = File(video.get(0)).length()
            println(lengthBeforeCom)

            if (lengthBeforeCom > 200000 && lengthBeforeCom < 70000000) {


                if (lengthBeforeCom > 100000 && lengthBeforeCom <= 5000000) {
                    pathOfImages = ArrayList()
                    pathOfImages.add(video.get(0))
                     complaintsPresenter.checkValidations(1, pathOfImages, etDescription)



                } else {

                    if (File(outPath).exists() && changeMedia == 1) {
                        changeMedia = 0
                        pathOfImages = ArrayList()
                        pathOfImages.add(outPath)
                            complaintsPresenter.checkValidations(1, pathOfImages, etDescription)


                    } else {
                        VideoCompress.compressVideoMedium(video.get(0), outPath, object :
                            VideoCompress.CompressListener {
                            override fun onStart() {
                                Log.e("Compressing", "Compress Start")
                                progressDialog.setCancelable(false)
                                progressDialog.setMessage("Processing Video...")
                                progressDialog.show()
                            }


                            override fun onSuccess() {
                                changeMedia = 1
                                try {
                                    if (progressDialog != null && progressDialog.isShowing) progressDialog.dismiss()
                                } catch (e: IllegalArgumentException) { // Handle or log or ignore

                                } catch (e: java.lang.Exception) { // Handle or log or ignore

                                }

//                        if (File(outPath).length() <= 50000000) {
                                pathOfImages = ArrayList()
                                pathOfImages.add(outPath)
                                  complaintsPresenter.checkValidations(1, pathOfImages, etDescription)


//                        } else {
//                            Toast.makeText(
//                                this@GeneralPublicActivity,
//                                "video size is very large",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
                            }

                            override fun onFail() {
                                Log.e("Compressing", "Compress Failed!")
                                try {
                                    if (progressDialog != null && progressDialog.isShowing) progressDialog.dismiss()
                                } catch (e: IllegalArgumentException) { // Handle or log or ignore

                                } catch (e: java.lang.Exception) { // Handle or log or ignore

                                }
                            }

                            override fun onProgress(percent: Float) {
                                progressDialog.setMessage("Compressing video " + percent.toInt() + "%")
                                Log.e("Compressing", percent.toString())

                            }
                        })
                    }
                }

            } else {
                Toast.makeText(this, "Video size should be 200 KB-70 MB ", Toast.LENGTH_LONG).show()


            }
        } else {
            Toast.makeText(this, "video is very short", Toast.LENGTH_LONG).show()
        }
    }

    private fun getLocale(): Locale? {
        val config = resources.configuration
        var sysLocale: Locale? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = getSystemLocale(config)
        } else {
            sysLocale = getSystemLocaleLegacy(config)
        }
        return sysLocale
    }
    fun getSystemLocaleLegacy(config: Configuration): Locale? {
        return config.locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun getSystemLocale(config: Configuration): Locale? {
        return config.locales[0]
    }

}