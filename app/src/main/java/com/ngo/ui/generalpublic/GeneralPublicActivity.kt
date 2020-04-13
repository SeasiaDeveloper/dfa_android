package com.ngo.ui.generalpublic

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.ComplaintRequest
import com.ngo.pojo.response.ComplaintResponse
import com.ngo.pojo.response.GetCrimeTypesResponse
import com.ngo.ui.generalpublic.presenter.PublicComplaintPresenter
import com.ngo.ui.generalpublic.presenter.PublicComplaintPresenterImpl
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.generalpublic.view.PublicComplaintView
import com.ngo.utils.*
import com.ngo.utils.Constants.GPS_REQUEST
import com.ngo.utils.Utilities.PERMISSION_ID
import kotlinx.android.synthetic.main.activity_public.*
import kotlinx.android.synthetic.main.activity_public.btnSubmit
import kotlinx.android.synthetic.main.activity_public.etDescription
import kotlinx.android.synthetic.main.activity_public.imgView
import kotlinx.android.synthetic.main.activity_public.sb_steps_5
import kotlinx.android.synthetic.main.activity_public.spTypesOfCrime
import kotlinx.android.synthetic.main.activity_public.toolbarLayout
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import kotlin.collections.ArrayList

class GeneralPublicActivity : BaseActivity(), View.OnClickListener, OnRangeChangedListener,
    PublicComplaintView {
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
        Utilities.requestPermissions(this)
        //GeneralPublicHomeFragment.fromIncidentDetailScreen=1
        GeneralPublicHomeFragment.change=0
    }

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
                val resultGallery = getMarshmallowPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
                if (resultGallery)
                    galleryIntent()
            }
            R.id.tvTakePhoto -> {
                path = ""
                val resultCamera = getMarshmallowPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
                if (resultCamera)
                    cameraIntent()
            }
            R.id.tvRecordVideo -> {
                Utilities.showMessage(this,getString(R.string.coming_soon))
                //commented for next ,milestone(server was overloaded)
              /*  path = ""
                val resultVideo = getMarshmallowPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
                if (resultVideo)
                    videoFromGalleryIntent()*/
            }

            R.id.tvTakeVideo -> {
                Utilities.showMessage(this,getString(R.string.coming_soon))
                //commented for next ,milestone(server was overloaded)
           /*     path = ""
                val resultVideo = getMarshmallowPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
                if (resultVideo)
                    recordVideo()*/
            }
            R.id.btnSubmit -> {
                complaintsPresenter.checkValidations(1, pathOfImages, etDescription.text.toString())
            }
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

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (getMarshmallowPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                    )
                )
                    cameraIntent()
            }
            Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent()
            }
        }
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Utilities.requestPermissions(this)
            else
                askForGPS()
        }
    }


    private fun askForGPS() {
        GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
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
        if (!Utilities.checkPermissions(this))
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
            }
        }
    }

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

                        imgView.setImageURI(imageUri)
                      //  path = getRealPathFromURI(imageUri!!)

                        //compression
                        val compClass = CompressImageUtilities()
                        val newPathString = compClass.compressImage(this@GeneralPublicActivity, getRealPathFromURI(imageUri!!))
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
                                val newPathString = compClass.compressImage(this@GeneralPublicActivity, cursor.getString(columnIndex!!))
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
        } else if (requestCode == REQUEST_CAMERA) {
            mediaType = "photos"
            if (intent != null && intent.getExtras() !=null &&  intent.getExtras()!!.get("data") !=null) {
                val photo = intent.getExtras()!!.get("data") as Bitmap
                imgView.setImageBitmap(photo)
                imgView.visibility = View.VISIBLE
                videoView.visibility = View.GONE
                val tempUri = getImageUri(applicationContext, photo)
                file = File(getRealPathFromURI(tempUri))

                //compression
                val compClass = CompressImageUtilities()
                val newPathString = compClass.compressImage(this@GeneralPublicActivity, getRealPathFromURI(tempUri))
                path = newPathString
            }
        }
        else if (requestCode == GPS_REQUEST) {
            isGPS = true
            getLocation()
        }
        else if (requestCode == SELECT_VIDEOS && resultCode == Activity.RESULT_OK || requestCode == SELECT_VIDEOS_KITKAT && resultCode == Activity.RESULT_OK) {
            mediaType = "videos"
            imgView.visibility = View.GONE
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
        }
        else if (requestCode == CAMERA_REQUEST_CODE_VEDIO && resultCode == Activity.RESULT_OK) {
            mediaType = "videos"
            imgView.visibility = View.GONE
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

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
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
        if(leftValue>=9){
            range = 10
        }
    }

    override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {


    }

    override fun showComplaintsResponse(complaintsResponse: ComplaintResponse) {
        dismissProgress()
        Utilities.showMessage(this, complaintsResponse.message!!)
        GeneralPublicHomeFragment.change = 1

        finish()
    }

    override fun showEmptyImageError() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.please_select_media))

    }

    override fun onValidationSuccess() {
        dismissProgress()

        val array = arrayOfNulls<String>(pathOfImages.size)
        var id: String? = null
        if (getCrimeTypesResponse != null) {
            id = getCrimeTypesResponse.data?.get(spTypesOfCrime.selectedItemPosition)?.id
        }
        if (isInternetAvailable()) {
            showProgress()
            val request = ComplaintRequest(
                id!!, //crimeType
                range,
                pathOfImages.toArray(array),
                etDescription.text.toString().trim(),
                lattitude,
                longitude,
                mediaType!!
            )
            complaintsPresenter.saveDetailsRequest(authorizationToken, request)
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
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypesOfCrime.setAdapter(adapter);
    }

    override fun getCrimeTyepLstFailure(error: String) {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.crime_types_error))
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }
}