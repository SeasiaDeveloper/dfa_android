package com.dfa.ui.generalpublic

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.databinding.DialogImageChoiceBinding
import com.dfa.pojo.request.ComplaintRequest
import com.dfa.pojo.response.ComplaintResponse
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.GetCrimeTypesResponse
import com.dfa.pojo.response.PStationsListResponse
import com.dfa.ui.emergency.view.EmergencyFragment
import com.dfa.ui.generalpublic.presenter.GetReportCrimeAlertDialog
import com.dfa.ui.generalpublic.presenter.PublicComplaintPresenter
import com.dfa.ui.generalpublic.presenter.PublicComplaintPresenterImpl
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.generalpublic.view.PublicComplaintView
import com.dfa.ui.login.view.LoginActivity
import com.dfa.utils.*
import com.dfa.utils.Constants.GPS_REQUEST
import com.dfa.utils.RealPathUtil.getCapturedImage
import com.dfa.utils.Utilities.PERMISSION_ID
import com.dfa.utils.Utilities.PERMISSION_ID_CAMERA
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.vincent.videocompressor.VideoCompress
import kotlinx.android.synthetic.main.activity_public.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("INACCESSIBLE_TYPE")
class GeneralPublicActivity : BaseActivity(), View.OnClickListener, OnRangeChangedListener,
    PublicComplaintView, GetReportCrimeAlertDialog {
    private lateinit var file: File
    private var longitude: String = ""
    private var lattitude: String = ""
    private var isGPS: Boolean = false
    private lateinit var locationManager: LocationManager
    private var crimeType: String = ""
    private var path: String = ""
    private var pathOfImages = ArrayList<String>()
    private val REQUEST_CAMERA = 0
    private var IMAGE_MULTIPLE = 1
    private var imageUri: Uri? = null
    private var authorizationToken: String? = ""
    private var complaintsPresenter: PublicComplaintPresenter = PublicComplaintPresenterImpl(this)
    private var range = 1
    private var mediaType: String? = null
    private var SELECT_VIDEOS: Int = 2
    private var SELECT_VIDEOS_KITKAT: Int = 2
    private var CAMERA_REQUEST_CODE_VEDIO: Int = 3
    private lateinit var mediaControls: MediaController
    private var provider: String = ""
    private var address: String = ""
    var mPhotoFile: File? = null
    var mCompressor: FileCompressor? = null
    var isOpenCamera: Boolean = true
    var isReadPermission: Boolean = true
    var resultGallery: Boolean = true
    var gps_enabled: Boolean = false
    var network_enabled: Boolean = false
    private val REQUEST_PERMISSIONS_GALLERY_VIDEO = 2
    private var isPermissionDialogRequired = true
    var police_id = ""
    private var districtList = ArrayList<DistResponse>()
    val PERMISSION_READ_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    val REQUEST_PERMISSIONS = 1
    var pstationResponse: PStationsListResponse? = null

    private lateinit var getCrimeTypesResponse: GetCrimeTypesResponse
    override fun getLayout(): Int {
        return R.layout.activity_public
    }

    override fun onResume() {
        super.onResume()
        if (!isGPS && !isPermissionDialogRequired) {
            askForGPS()
        }
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.report_incident)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        setListeners()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!Utilities.checkPermissions(this)) {
            Utilities.requestPermissions(this)
        } else {
            //  askForGPS()
            isPermissionDialogRequired = false
        }


        /*  var isLocationPermission = Utilities.checkPermissions(this)
          if (!isLocationPermission) {
              requestPermissions(this)
          }*/

        //  getLocation()

        //GeneralPublicHomeFragment.fromIncidentDetailScreen=1

        // GeneralPublicHomeFragment.change = 0
        GeneralPublicHomeFragment.changeThroughIncidentScreen = 0
        clear_image.setOnClickListener(this)

        /* scroll_view.setOnTouchListener(object : View.OnTouchListener {
             var startClickTime: Long = 0
             override fun onTouch(p0: View?,event: MotionEvent?): Boolean {
                 if (event?.getAction() == MotionEvent.ACTION_DOWN) {
                     startClickTime = System.currentTimeMillis();
                 } else if (event?.getAction() == MotionEvent.ACTION_UP) {
                     if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                         // Touch was a simple tap. Do whatever.
                         mediaControls.visibility = View.GONE
                         mediaControls.setAnchorView(videoView)
                         videoView.setMediaController(mediaControls)
                     } else {
                         // Touch was a not a simple tap.
                         mediaControls.visibility = View.GONE
                         mediaControls.setAnchorView(videoView)
                         videoView.setMediaController(mediaControls)
                     }
                 }
                 return true;
             }
         })*/
    }

    //
    private fun setListeners() {
        // tvSelectPhoto.setOnClickListener(this)
        tvTakePhoto.setOnClickListener(this)
        img_delete.setOnClickListener(this)

        // tvRecordVideo.setOnClickListener(this)
        tvTakeVideo.setOnClickListener(this)
        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        if (isInternetAvailable()) {
            showProgress()
            complaintsPresenter.onGetCrimeTypes(authorizationToken)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }

        spTypesOfCrime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // nothing to do
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                crimeType = spTypesOfCrime.selectedItem.toString()
            }
        }
        sb_steps_5.setOnRangeChangedListener(this)
        btnSubmit.setOnClickListener(this)
        btnSubmitParticular.setOnClickListener(this)

    }

    override fun handleKeyboard(): View {
        return publicParentLayout
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
//            R.id.tvSelectPhoto -> {
//                path = ""
//                getGalleryPermission()
//                if (resultGallery)
//                    galleryIntent()
//            }
            R.id.tvTakePhoto -> {
//                path = ""
//                getCameraPermission()
//                if (isOpenCamera) {
//                    cameraIntent()
//                }

                showChoiceDialog("photo")


            }

            R.id.img_delete -> {
                path = ""
                pathOfImages = ArrayList()
                mediaControls.visibility = View.GONE
                mediaControls.setAnchorView(videoView)
                videoView.setMediaController(mediaControls)
                video_parent.visibility = View.GONE

                if (videoView != null) {
                    videoView.stopPlayback()
                }
            }
//            R.id.tvRecordVideo -> {
//               // Utilities.showMessage(this, getString(R.string.coming_soon))
//               path = ""
//                if (CheckRuntimePermissions.checkMashMallowPermissions(
//                        this,
//                        PERMISSION_READ_STORAGE, REQUEST_PERMISSIONS_GALLERY_VIDEO
//                    )
//                ) {
//                    videoFromGalleryIntent()
//                }
//            }

            R.id.tvTakeVideo -> {
//                //Utilities.showMessage(this, getString(R.string.coming_soon))
//                path = ""
//                if (CheckRuntimePermissions.checkMashMallowPermissions(
//                        this,
//                        PERMISSION_READ_STORAGE, REQUEST_PERMISSIONS
//                    )
//                ) {
//                    recordVideo()
//                }

                showChoiceDialog("video")

            }
            R.id.btnSubmit -> {

                police_id = ""
                spDistrict.visibility = View.GONE
                spPolice.visibility = View.GONE
                if (etDescription.text.toString().trim() == "") {
                    Utilities.showMessage(this, "Please enter description")

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
                                        etDescription.text.toString()
                                    )
                                }
                            }
                        } else {
                            complaintsPresenter.checkValidations(
                                1,
                                pathOfImages,
                                etDescription.text.toString()
                            )
                        }
                    } else {
                        complaintsPresenter.checkValidations(
                            1,
                            pathOfImages,
                            etDescription.text.toString()
                        )
                    }
                }
            }

            R.id.btnSubmitParticular -> {

                if (etDescription.text.toString().trim() == "") {
                    Utilities.showMessage(this, "Please enter description")

                } else if (pathOfImages.size == 0) {

                    Utilities.showMessage(this, getString(R.string.please_select_media))


                } else {
                    spDistrict.visibility = View.VISIBLE
                    scroll_view.post(Runnable { scroll_view.fullScroll(View.FOCUS_DOWN) })
                    if (mediaType.equals("videos")) {
                        if (pathOfImages.size > 0) {
                            if (!pathOfImages.get(0).isEmpty()
                            ) {

                                if (File(pathOfImages.get(0)).length() > 5000) {
                                    videoCompressorCustom(pathOfImages)
                                    getDistrictList()
                                } else {

                                    getDistrictList()
//                                    complaintsPresenter.checkValidations(
//                                        1,
//                                        pathOfImages,
//                                        etDescription.text.toString()
//                                    )
                                }
                            }
                        } else {
                            getDistrictList()
//                            complaintsPresenter.checkValidations(
//                                1,
//                                pathOfImages,
//                                etDescription.text.toString()
//                            )
                        }
                    } else {
                        getDistrictList()
//                        complaintsPresenter.checkValidations(
//                            1,
//                            pathOfImages,
//                            etDescription.text.toString()
//                        )
                    }
                }
            }

            R.id.clear_image -> {
                pathOfImages = ArrayList()
                imageview_layout.visibility = View.GONE
                val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)
                try {
                    Glide.with(this).asBitmap().load(R.drawable.noimage).apply(options)
                        .into(imgView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun getDistrictList() {

        if (districtList.size == 0) {
            Utilities.showProgress(this)
            complaintsPresenter.hitDistricApi()
        } else getDistrictDropDown(districtList[0])
    }


    private fun showChoiceDialog(type: String) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this@GeneralPublicActivity)
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(this@GeneralPublicActivity),
            R.layout.dialog_image_choice,
            null,
            false
        ) as DialogImageChoiceBinding


        val camera = binding.cvCamera
        val gallery = binding.cvGallery
        // Create the AlertDialog object and return it
        camera.setOnClickListener {
            // mInterface.photoFromCamera(mKey)

            if (type == "photo") {

                path = ""
                getCameraPermission()
                if (isOpenCamera) {
                    cameraIntent()
                }

            } else {

                path = ""
                if (CheckRuntimePermissions.checkMashMallowPermissions(
                        this,
                        PERMISSION_READ_STORAGE, REQUEST_PERMISSIONS
                    )
                ) {
                    recordVideo()
                }

            }



            dialog.dismiss()
        }
        gallery.setOnClickListener {
            // mInterface.photoFromGallery(mKey)

            if (type == "photo") {

                path = ""
                getGalleryPermission()
                if (resultGallery)
                    galleryIntent()

            } else {

                path = ""
                if (CheckRuntimePermissions.checkMashMallowPermissions(
                        this,
                        PERMISSION_READ_STORAGE, REQUEST_PERMISSIONS_GALLERY_VIDEO
                    )
                ) {
                    videoFromGalleryIntent()
                }


            }



            dialog.dismiss()
        }


        builder.setView(binding.root)
        dialog = builder.create()
        dialog.show()
    }


    private fun videoCompressorCustom(video: ArrayList<String>) {
        if (!video.get(0).isEmpty() && File(video.get(0)).length() > 0) {
            var myDirectory = File(Environment.getExternalStorageDirectory(), "Pictures");

            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }

            var outPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + File.separator + "VID_" + SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    getLocale()
                ).format(Date()) + ".mp4";


            var progressDialog = ProgressDialog(this)

            var lengthBeforeCom = File(video.get(0)).length()
            println(lengthBeforeCom)

            if (lengthBeforeCom > 500000 && lengthBeforeCom < 70000000) {


                if(lengthBeforeCom> 500000  && lengthBeforeCom<= 1000000)
                {
                    pathOfImages = ArrayList()
                    pathOfImages.add(video.get(0))
                    complaintsPresenter.checkValidations(
                        1,
                        pathOfImages,
                        etDescription.text.toString()
                    )
                }

                else {

                    VideoCompress.compressVideoMedium(video.get(0), outPath, object :
                        VideoCompress.CompressListener {
                        override fun onStart() {
                            Log.e("Compressing", "Compress Start")
                            progressDialog.setCancelable(false)
                            progressDialog.setMessage("Processing Video...")
                            progressDialog.show()
                        }


                        override fun onSuccess() {

                            try {
                                if (progressDialog != null && progressDialog.isShowing) progressDialog.dismiss()
                            } catch (e: IllegalArgumentException) { // Handle or log or ignore

                            } catch (e: java.lang.Exception) { // Handle or log or ignore

                            }

//                        if (File(outPath).length() <= 50000000) {
                            pathOfImages = ArrayList()
                            pathOfImages.add(outPath)
                            complaintsPresenter.checkValidations(
                                1,
                                pathOfImages,
                                etDescription.text.toString()
                            )
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

            } else {
                Toast.makeText(this, "Video size should be 500 KB-70 MB ", Toast.LENGTH_LONG).show()


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


    private fun getCameraPermission() {
        if (!Utilities.checkCameraPermissions(this)) {
            Utilities.requestPermissionsForCamera(this)
            isOpenCamera = false
        } else {
            isOpenCamera = true
        }
    }

    private fun getGalleryPermission() {
        if (!Utilities.checkGalleryPermissions(this)) {
            Utilities.requestPermissionsForGallery(this)
            resultGallery = false
        } else {
            resultGallery = true
        }
    }

    private fun recordVideo() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120)
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, CAMERA_REQUEST_CODE_VEDIO)
        }
    }

/*
    private fun selectImage() {
        val items = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.choose_from_gallery),
            "Cancel"
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            if (items[item] == getString(R.string.take_photo)) {
                userChoosenTask = getString(R.string.take_photo)
                val resultCamera = getMarshmallowPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
                if (resultCamera)
                    cameraIntent()

            } else if (items[item] == getString(R.string.choose_from_gallery)) {
                userChoosenTask = getString(R.string.choose_from_gallery)
                val resultGallery = getMarshmallowPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
                if (resultGallery)
                    galleryIntent()

            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }
*/

    private fun videoFromGalleryIntent() {
        if (Build.VERSION.SDK_INT < 19) {
//            val intent = Intent()
//            intent.setType("video/*")
//            intent.setAction(Intent.ACTION_GET_CONTENT)
//            startActivityForResult(Intent.createChooser(intent, "Select videos"), SELECT_VIDEOS)


//
//                Intent intent = new Intent();
//                intent.setType("video/*");
//                intent.setAction(Intent.ACTION_PICK);
//                BaseCameraActivity.this.startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO);
            val intent = Intent()
            intent.setTypeAndNormalize("video/*")
            intent.action = Intent.ACTION_GET_CONTENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                SELECT_VIDEOS
            )


        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            // intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("video/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, SELECT_VIDEOS_KITKAT)
        }
    }

    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"    //"image/* video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"), IMAGE_MULTIPLE)
    }

    private fun getMarshmallowPermission(
        permissionRequest: String,
        requestCode1: String,
        requestCode: Int
    ): Boolean {
        return Utilities.checkPermission(
            this,
            permissionRequest,
            requestCode
        )
    }

    /**
     * Capture image from camera
     */
    private fun dispatchTakePictureIntent() {
        var takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                var photoURI = FileProvider.getUriForFile(
                    this,
                    "com.dfango.android" + ".provider",
                    photoFile
                )
                mPhotoFile = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_CAMERA)
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        var timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        var mFileName = "JPEG_" + timeStamp + "_"
        var storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var mFile = File.createTempFile(mFileName, ".jpg", storageDir)
        return mFile
    }


    private fun cameraIntent() {
        /*  val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          startActivityForResult(intent, REQUEST_CAMERA)*/
        // ImagePicker.cameraOnly().start(this)
        dispatchTakePictureIntent()
    }

    private fun getCaptureImageOutputUri(): Uri {
        var outputFileUri: Uri? = null
        var getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.getPath(), "profile.png"))
        }
        return outputFileUri!!
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
/*            Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (getMarshmallowPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                    )
                )
                    isPermissionsDone = true
            }

            Utilities.MY_PERMISIIONS_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (getMarshmallowPermission(
                        Manifest.permission.CAMERA,
                        Utilities.MY_PERMISIIONS_CAMERA
                    )
                )
                    resultCamera2 = true
            }*/

            Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                resultGallery = true
                galleryIntent()
            }

            PERMISSION_ID_CAMERA -> {
                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED) && (grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    //  askForGPS()
                    isOpenCamera = true
                    cameraIntent()
                }
            }

            PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                // Utilities.requestPermissions(this)
                else
                    if (!isGPS) {
                        askForGPS()
                    }
            }

            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordVideo()
                }
            }

            REQUEST_PERMISSIONS_GALLERY_VIDEO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    videoFromGalleryIntent()
                }
            }
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
                // Request location updates
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0f,
                    mLocationListener
                )
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

    private val mLocationListener = object : LocationListener {
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

        override fun onLocationChanged(location: Location) {
            //your code here
            if (location != null) {
                val latti = location.latitude
                val longi = location.longitude
                lattitude = (latti).toString()
                longitude = (longi).toString()
                PreferenceHandler.writeString(
                    this@GeneralPublicActivity,
                    PreferenceHandler.LAT,
                    lattitude
                )
                PreferenceHandler.writeString(
                    this@GeneralPublicActivity,
                    PreferenceHandler.LNG,
                    longitude
                )
                address = Utilities.getAddressFromLatLong(
                    lattitude.toDouble(),
                    longitude.toDouble(),
                    this@GeneralPublicActivity
                )
            }
        }
    }

    fun imageCompressor(selectedImage: Uri): Bitmap {

        var imageStream: InputStream? = null
        try {
            imageStream = contentResolver.openInputStream(
                selectedImage
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val bmp: Bitmap = BitmapFactory.decodeStream(imageStream)

        var stream: ByteArrayOutputStream? = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream!!.toByteArray()
        try {
            stream!!.close()
            stream = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmp
    }

    fun getImageUri(
        inContext: Context,
        inImage: Bitmap
    ): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != intent) {
            if (intent.data != null) {
                mediaType = "photos"
                imageUri = intent.data
                if (imageUri != null) {
                    try {
                        ///   BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri!!))
                        imgView.visibility = View.VISIBLE
                        video_parent.visibility = View.GONE
                        imageview_layout.visibility = View.VISIBLE
                        imgView.setImageURI(imageUri)
                        //  path = getRealPathFromURI(imageUri!!)
                        //compression
//                        val compClass = CompressImageUtilities()
//                        val newPathString = compClass.compressImage(
//                            this@GeneralPublicActivity,
//                            getRealPathFromURI(imageUri!!)
//                        )

                        var bitmap = imageCompressor(imageUri!!)

                        var newPathString = getImageUri(this, bitmap)


                        Log.d(
                            "GeneralPublicActivity",
                            "gettingSiseUri" + File(imageUri!!.path).length()
                        )
                        Log.d(
                            "GeneralPublicActivity",
                            "compressedSize" + File(newPathString!!.path).length()
                        )

//                        var newPathString = Compressor.getDefault(this).compressToFile(File(imageUri.toString()));


                        path = FileUtils.getPath(this, newPathString)

                        pathOfImages = ArrayList()
                        pathOfImages.add(path)
                    } catch (e: Exception) {
                        try {
                            val wholeID = DocumentsContract.getDocumentId(imageUri)
                            val id =
                                wholeID.split((":").toRegex()).dropLastWhile({ it.isEmpty() })
                                    .toTypedArray()[1]
                            val column = arrayOf(MediaStore.Images.Media.DATA)
                            val sel = MediaStore.Images.Media._ID + "=?"
                            val cursor = getContentResolver().query(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, sel, arrayOf(id), null
                            )
                            val columnIndex = cursor?.getColumnIndex(column[0])
                            if (cursor!!.moveToFirst()) {
                                //  path = cursor.getString(columnIndex!!)
                                //compression
                                val compClass = CompressImageUtilities()
                                val newPathString = compClass.compressImage(
                                    this@GeneralPublicActivity,
                                    cursor.getString(columnIndex!!)
                                )
                                path = newPathString
                                pathOfImages = ArrayList()
                                pathOfImages.add(path)
                            }
                            cursor.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }


        //else if (ImagePicker.shouldHandle(requestCode, resultCode, intent)) {
        else if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            mediaType = "photos"
            if (mPhotoFile != null) {
                /*
                 one code
                 if (intent != null && intent.getExtras() != null && intent.getExtras()!!.get("data") != null) {
                     val photo = intent.getExtras()!!.get("data") as Bitmap
                     imageview_layout.visibility = View.VISIBLE
                     imgView.visibility = View.VISIBLE
                     videoView.visibility = View.GONE
                     val tempUri = getImageUriWhenTakePhoto(applicationContext, photo)
                     imgView.setImageURI(tempUri)
                     file = File(getRealPathFromURI(tempUri))
                     path = getRealPathFromURI(tempUri)*/

                val tempUri = Uri.fromFile(mPhotoFile)
                imageview_layout.visibility = View.VISIBLE
                imgView.visibility = View.VISIBLE

                video_parent.visibility = View.GONE
                imgView.setImageURI(tempUri)

                //val tempUri = getImageUriWhenTakePhoto(applicationContext, images.get(0).path)
                var bitmap = getCapturedImage(tempUri, this)


                if (bitmap != null) {
                    val requiredImage =
                        RealPathUtil.imageRotateIfRequired(mPhotoFile?.absolutePath!!, bitmap)
                    imgView.setImageBitmap(requiredImage)
                    //path = mPhotoFile?.absolutePath!!
                    var newPathString = getImageUri(this, requiredImage)
                    path = FileUtils.getPath(this, newPathString)
                    pathOfImages = ArrayList<String>()
                    pathOfImages.add(path)
                    // }

                }
            }
        } else if (requestCode == GPS_REQUEST) {
            isGPS = true
            getLocation()
        } else if (requestCode == SELECT_VIDEOS && resultCode == Activity.RESULT_OK || requestCode == SELECT_VIDEOS_KITKAT && resultCode == Activity.RESULT_OK) {
            mediaType = "videos"

            if (intent?.data != null) {


                // String selectedVideoPath = getAbsolutePath(this, data.getData());

                val imagePath = intent.getData()
                //  if (imagePath!!.contains("video")) {
//                    val realpath = RealPathUtil.getRealPath(this, intent.data!!)
//                    val thumbnail = RealPathUtil.getThumbnailFromVideo(realpath!!)
                // imgView.setImageBitmap(thumbnail)

                if (imagePath != null) {

                    val retriever =
                        MediaMetadataRetriever()
                    retriever.setDataSource(this, imagePath)
                    val time =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val timeInMillisec = time.toLong()
                    retriever.release()

                    if (timeInMillisec >= 5000) {

                        val intent = Intent(this, TrimmerActivity::class.java)
                        intent.putExtra("path", FileUtils.getPath(this, imagePath))
                        startActivityForResult(intent, 5)
                    } else {
                        Toast.makeText(this, "Video length is too short", Toast.LENGTH_LONG).show()
                    }
                }


//                    showVideo(intent.data.toString())
                //    }
//                pathOfImages = ArrayList<String>()
//                pathOfImages.add(RealPathUtil.getRealPath(this, intent.data!!).toString())
            }
        } else if (requestCode == 5) {
            try {
                if (intent!!.getStringExtra("filePath") != null) {
                    //Toast.makeText(this,""+data.getStringExtra("filePath"),Toast.LENGTH_LONG).show();
                    var path = intent!!.getStringExtra("filePath")
                    //  onBackPress = "1"
                    var ountDownTimer = object : CountDownTimer(1000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                        }

                        override fun onFinish() {
                            try {
                                showVideo(path)
                            } catch (e: Exception) {

                            }
                        }
                    }.start()
                    imgView.visibility = View.GONE
                    imageview_layout.visibility = View.GONE
                    video_parent.visibility = View.VISIBLE
                    pathOfImages = ArrayList<String>()
                    pathOfImages.add(path)
                }
            } catch (e: java.lang.Exception) {
            }
        } else if (requestCode == CAMERA_REQUEST_CODE_VEDIO && resultCode == Activity.RESULT_OK) {
            mediaType = "videos"
            imgView.visibility = View.GONE
            imageview_layout.visibility = View.GONE
            video_parent.visibility = View.VISIBLE
            val videoUri = intent?.getData()
            path = getRealPathFromURI(videoUri!!)
            pathOfImages = ArrayList()
            pathOfImages.add(path)
            showVideo(path)
        }
    }

    fun showVideo(videoUri: String) {
        mediaControls = MediaController(this@GeneralPublicActivity)
        mediaControls.visibility = View.VISIBLE
        mediaControls.setAnchorView(videoView)
        videoView.setMediaController(mediaControls)
        videoView.setVideoURI(Uri.parse(videoUri))
        videoView.setBackgroundColor(Color.TRANSPARENT)
        videoView.seekTo(100)
        videoView.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                return true
            }
        })

        videoView.setOnClickListener(this)

    }

    private fun getRealPathFromURI(uri: Uri): String {
        if (contentResolver != null) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                pathOfImages = ArrayList()
                pathOfImages.add(path)
                cursor.close()
            }
        }
        return path
    }

    override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
    }

    override fun onRangeChanged(
        view: RangeSeekBar?,
        leftValue: Float,
        rightValue: Float,
        isFromUser: Boolean
    ) {
        // Utilities.showMessage(this, leftValue.toString())
        range = Math.ceil(leftValue.toDouble()).toInt()
        if (leftValue >= 9) {
            range = 10
        }
    }

    override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {


    }

    override fun showComplaintsResponse(complaintsResponse: ComplaintResponse) {
        dismissProgress()
        Utilities.showMessage(this, complaintsResponse.message!!)
        //GeneralPublicHomeFragment.change = 1
        GeneralPublicHomeFragment.changeThroughIncidentScreen = 1
        finish()

        val fdelete = File(pathOfImages.get(0))
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                //System.out.println("file Deleted :" + uri.getPath())
            } else {
                //System.out.println("file not Deleted :" + uri.getPath())
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
        } else {
            lattitude = ""
            longitude = ""
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
                var location = locationManager.getLastKnownLocation(provider);

                // Initialize the location fields
                if (location != null) {
                    //System.out.println("Provider " + provider + " has been selected.");
                    val latti = location.latitude
                    val longi = location.longitude
                    lattitude = (latti).toString()
                    longitude = (longi).toString()
                    PreferenceHandler.writeString(
                        this@GeneralPublicActivity,
                        PreferenceHandler.LAT,
                        lattitude
                    )
                    PreferenceHandler.writeString(
                        this@GeneralPublicActivity,
                        PreferenceHandler.LNG,
                        longitude
                    )
                    address = Utilities.getAddressFromLatLong(
                        lattitude.toDouble(),
                        longitude.toDouble(),
                        this@GeneralPublicActivity
                    )
                }
            }



            if (lattitude.equals("")) {
                askForGPS()
            }

        }
        com.dfa.utils.alert.AlertDialog.reportCrimeAlertDialog(this, this)
    }

    override fun getCallback() {
        var id: String? = null
        if (getCrimeTypesResponse != null) {
            id = getCrimeTypesResponse.data?.get(spTypesOfCrime.selectedItemPosition)?.id
        }
        val array = arrayOfNulls<String>(pathOfImages.size)

        if (isInternetAvailable()) {
            showProgress()
            val request = ComplaintRequest(
                id!!, //crimeType
                range,
                pathOfImages.toArray(array),
                etDescription.text.toString().trim(),
                lattitude,
                longitude,
                mediaType!!,
                address,
                police_id
            )
            complaintsPresenter.saveDetailsRequest(
                authorizationToken,
                request,
                this@GeneralPublicActivity
            )
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
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
        val distArray = distValueList.toArray(arrayOfNulls<String>(distValueList.size))
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, distArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTypesOfCrime.setAdapter(adapter)
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

    override fun getpStationSuccess(response: PStationsListResponse) {
        dismissProgress()
        //districtList.add(response)
        getStationDropDown(response)
    }


    fun getDistrictDropDown(response: DistResponse) {
        EmergencyFragment.staticDistValueList = response

        val distValueList = ArrayList<String>()
        spPolice.visibility=View.GONE
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
        spDistrict.setAdapter(adapter)

        try {


            spDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                                this@GeneralPublicActivity,
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

    fun getStationDropDown(response: PStationsListResponse) {
        val distValueList = ArrayList<String>()
        pstationResponse = response

        if (response.data?.size == 0) {
            spPolice.visibility = View.GONE
            Utilities.showMessage(this, "No police station available in selected district")
        } else {
            spPolice.visibility = View.VISIBLE
            scroll_view.post(Runnable { scroll_view.fullScroll(View.FOCUS_DOWN) })

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
        spPolice.setAdapter(adapter)

        try {


            spPolice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

                        if (etDescription.text.toString().trim() == "") {
                            Utilities.showMessage(this@GeneralPublicActivity, "Please enter description")

                        }
                        else {
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
                                                etDescription.text.toString()
                                            )
                                        }
                                    }
                                } else {
                                    complaintsPresenter.checkValidations(
                                        1,
                                        pathOfImages,
                                        etDescription.text.toString()
                                    )
                                }
                            } else {
                                complaintsPresenter.checkValidations(
                                    1,
                                    pathOfImages,
                                    etDescription.text.toString()
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


    override fun showServerError(error: String) {
        dismissProgress()
        if (error.equals(Constants.TOKEN_ERROR)) {
            //ForegroundService.stopService(this@GeneralPublicActivity)
            finish()
            PreferenceHandler.clearPreferences(this@GeneralPublicActivity)
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            Utilities.showMessage(this, error)
        }
    }


}