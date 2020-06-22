package com.dfa.ui.generalpublic

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.pojo.request.ComplaintRequest
import com.dfa.pojo.response.ComplaintResponse
import com.dfa.pojo.response.GetCrimeTypesResponse
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
import com.dfa.utils.Utilities.requestPermissions
import com.dfa.utils.FileCompressor
import com.dfa.utils.GpsUtils
import kotlinx.android.synthetic.main.activity_public.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GeneralPublicActivity : BaseActivity(), View.OnClickListener, OnRangeChangedListener,
    PublicComplaintView,GetReportCrimeAlertDialog {
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

    private lateinit var getCrimeTypesResponse: GetCrimeTypesResponse
    override fun getLayout(): Int {
        return R.layout.activity_public
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
        provider = LocationManager.GPS_PROVIDER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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

        } else {
            requestPermissions(this)
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
    }

    //
    private fun setListeners() {
        tvSelectPhoto.setOnClickListener(this)
        tvTakePhoto.setOnClickListener(this)

        tvRecordVideo.setOnClickListener(this)
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
    }

    override fun handleKeyboard(): View {
        return publicParentLayout
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvSelectPhoto -> {
                path = ""
                getGalleryPermission()
                if (resultGallery)
                    galleryIntent()
            }
            R.id.tvTakePhoto -> {
                path = ""
                getCameraPermission()
                if (isOpenCamera) {
                    cameraIntent()
                }
            }
            R.id.tvRecordVideo -> {
                //Utilities.showMessage(this, getString(R.string.coming_soon))
                //commented for next ,milestone(server was overloaded)
                  path = ""
                  val resultVideo = getMarshmallowPermission(
                      Manifest.permission.WRITE_EXTERNAL_STORAGE,
                      Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                  )
                  if (resultVideo)
                      videoFromGalleryIntent()
            }

            R.id.tvTakeVideo -> {
                //Utilities.showMessage(this, getString(R.string.coming_soon))
                //commented for next ,milestone(server was overloaded)
                     path = ""
                     val resultVideo = getMarshmallowPermission(
                         Manifest.permission.READ_EXTERNAL_STORAGE,
                         Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                     )
                     if (resultVideo)
                         recordVideo()
            }
            R.id.btnSubmit -> {
                complaintsPresenter.checkValidations(1, pathOfImages, etDescription.text.toString())
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
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, CAMERA_REQUEST_CODE_VEDIO);
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
            val intent = Intent()
            intent.setType("video/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select videos"), SELECT_VIDEOS)
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

    private fun getMarshmallowPermission(permissionRequest: String, requestCode: Int): Boolean {
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
        }

        if (requestCode == PERMISSION_ID_CAMERA) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED) && (grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                //  askForGPS()
                isOpenCamera = true
                cameraIntent()
            }
        }

        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            // Utilities.requestPermissions(this)
            else
                askForGPS()
        }
    }


    private fun askForGPS() {
        GpsUtils(this)
            .turnGPSOn(object : GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
                /* if(!isGPS)
            askForGPS()*/
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

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != intent) {
            if (intent.data != null) {
                mediaType = "photos"
                imageUri = intent.data

                if (imageUri != null) {
                    try {
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri!!))
                        imgView.visibility = View.VISIBLE
                        videoView.visibility = View.GONE
                        imageview_layout.visibility = View.VISIBLE
                        imgView.setImageURI(imageUri)
                        //  path = getRealPathFromURI(imageUri!!)

                        //compression
                        val compClass = CompressImageUtilities()
                        val newPathString = compClass.compressImage(
                            this@GeneralPublicActivity,
                            getRealPathFromURI(imageUri!!)
                        )
                        path = newPathString

                        pathOfImages = ArrayList()
                        pathOfImages.add(path)
                    } catch (e: Exception) {
                        try {
                            val wholeID = DocumentsContract.getDocumentId(imageUri)
                            val id =
                                wholeID.split((":").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
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
                videoView.visibility = View.GONE
                imgView.setImageURI(tempUri)

                //val tempUri = getImageUriWhenTakePhoto(applicationContext, images.get(0).path)
                var bitmap = getCapturedImage(tempUri, this)
                if (bitmap != null) {
                    val requiredImage =
                        RealPathUtil.imageRotateIfRequired(mPhotoFile?.absolutePath!!, bitmap)
                    imgView.setImageBitmap(requiredImage)
                    path =
                            /*compClass.storeImage(decoded,this@GeneralPublicActivity)?.absolutePath!!*/
                        mPhotoFile?.absolutePath!!
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
            imgView.visibility = View.GONE
            imageview_layout.visibility = View.GONE
            videoView.visibility = View.VISIBLE
            if (intent?.data != null) {
                val imagePath = intent.getData()?.getPath()
                if (imagePath!!.contains("video")) {
                    val realpath = RealPathUtil.getRealPath(this, intent.data!!)
                    val thumbnail = RealPathUtil.getThumbnailFromVideo(realpath!!)
                    // imgView.setImageBitmap(thumbnail)

                    showVideo(intent.data.toString())
                }
                pathOfImages = ArrayList<String>()
                pathOfImages.add(RealPathUtil.getRealPath(this, intent.data!!).toString())
            }
        } else if (requestCode == CAMERA_REQUEST_CODE_VEDIO && resultCode == Activity.RESULT_OK) {
            mediaType = "videos"
            imgView.visibility = View.GONE
            imageview_layout.visibility = View.GONE
            videoView.visibility = View.VISIBLE
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
        videoView.seekTo(100) // displays thumbnail of the video
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
    }

    override fun showEmptyImageError() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.please_select_media))

    }

    override fun onValidationSuccess() {
        dismissProgress()

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

        com.dfa.utils.alert.AlertDialog.reportCrimeAlertDialog(this,this)
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
                address
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

    override fun showServerError(error: String) {
        dismissProgress()
        if (error.equals(Constants.TOKEN_ERROR)) {
            ForegroundService.stopService(this@GeneralPublicActivity)
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