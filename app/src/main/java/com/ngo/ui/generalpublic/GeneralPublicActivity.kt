package com.ngo.ui.generalpublic

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.ComplaintRequest
import com.ngo.pojo.response.ComplaintResponse
import com.ngo.ui.generalpublic.presenter.PublicComplaintPresenter
import com.ngo.ui.generalpublic.presenter.PublicComplaintPresenterImpl
import com.ngo.ui.generalpublic.view.GeneralPublicHomeActivity
import com.ngo.ui.generalpublic.view.PublicComplaintView
import com.ngo.utils.Constants.GPS_REQUEST
import com.ngo.utils.Constants.LOCATION_REFRESH_DISTANCE
import com.ngo.utils.Constants.LOCATION_REFRESH_TIME
import com.ngo.utils.GpsUtils
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
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
import java.lang.Integer.parseInt


class GeneralPublicActivity : BaseActivity(), View.OnClickListener, OnRangeChangedListener,
    PublicComplaintView {


    private lateinit var file: File
    private  var longitude: String=""
    private  var lattitude: String=""
    private var isGPS: Boolean = false
    private lateinit var locationManager: LocationManager
    private var crimeType: String = ""
    private var path: String=""
    private val REQUEST_CAMERA = 0
    private var IMAGE_MULTIPLE = 1
    private var userChoosenTask: String? = null
    private var imageUri: Uri? = null
    private val REQUEST_TAKE_GALLERY_VIDEO = 1889

    private var complaintsPresenter: PublicComplaintPresenter = PublicComplaintPresenterImpl(this)
private var  range=1
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

    }

    private fun setListeners() {
        tvSelectPhoto.setOnClickListener(this)
        tvTakePhoto.setOnClickListener (this)
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
                crimeType =spTypesOfCrime.selectedItem.toString()
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
                val resultGallery = getMarshmallowPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
                if (resultGallery)
                    galleryIntent()
            }
            R.id.tvTakePhoto ->{
                val resultCamera = getMarshmallowPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
                if (resultCamera)
                    cameraIntent()
            }
            R.id.btnSubmit -> {
                complaintsPresenter.checkValidations(1,path,etDescription.text.toString())
            }
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


    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
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
        GpsUtils(this).turnGPSOn(object:GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable:Boolean) {
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
        if(!Utilities.checkPermissions(this))
            Utilities.requestPermissions(this)
        else
            try {
                // Request location updates
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, mLocationListener)
            } catch(ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available")
            }
    }

    private val mLocationListener = object:LocationListener {
      override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
      }

      override fun onProviderEnabled(p0: String?) {
      }

      override fun onProviderDisabled(p0: String?) {
      }

      override fun onLocationChanged(location: Location) {
            //your code here
            if (location != null)
            {
                val latti = location.latitude
                val longi = location.longitude
                lattitude = (latti).toString()
                longitude = (longi).toString()
                PreferenceHandler.writeString(this@GeneralPublicActivity,PreferenceHandler.LAT,lattitude)
                PreferenceHandler.writeString(this@GeneralPublicActivity,PreferenceHandler.LNG,longitude)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != data) {
            if (data.data != null) {
                imageUri = data.data
                val wholeID = DocumentsContract.getDocumentId(imageUri)
                val id = wholeID.split((":").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
                val column = arrayOf<String>(MediaStore.Images.Media.DATA)
                val sel = MediaStore.Images.Media._ID + "=?"
                val cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, arrayOf<String>(id), null)
                val columnIndex = cursor?.getColumnIndex(column[0])
                if (cursor!!.moveToFirst())
                {
                    path = cursor.getString(columnIndex!!)
                }
                cursor.close()
                imgView.visibility = View.VISIBLE
                imgView.setImageURI(imageUri)
            }
        } else if (requestCode == REQUEST_CAMERA) {
            if (null != data) {
                val photo = data?.getExtras()?.get("data") as Bitmap
                imgView.setImageBitmap(photo)
                imgView.visibility = View.VISIBLE
                val tempUri = getImageUri(applicationContext, photo)
                file = File(getRealPathFromURI(tempUri))
            }
        }else if (requestCode == GPS_REQUEST) {
            isGPS = true
            getLocation()
        }

    }

    private fun getImageUri(inContext: Context, inImage:Bitmap):Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
    private fun getRealPathFromURI(uri:Uri):String {
        if (contentResolver != null)
        {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null)
            {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
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
        range=Math.ceil(leftValue.toDouble()).toInt()
    }

    override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {


    }

    override fun showComplaintsResponse(complaintsResponse: ComplaintResponse) {
        dismissProgress()
        Utilities.showMessage(this,complaintsResponse.message)
        GeneralPublicHomeActivity.change=1
        finish()
    }

    override fun showEmptyImageError() {
        dismissProgress()
        Utilities.showMessage(this,getString(R.string.please_select_image))

    }

    override fun onValidationSuccess() {
        dismissProgress()
        if (isInternetAvailable()) {
            showProgress()
            var name ="Nabam Serbang"
            var contact="911234567890"
            var email ="nabam@gmail.com"
            val request = ComplaintRequest(
               name,
                contact,
                email,
                crimeType,
                range,
                path,
                etDescription.text.toString().trim(),
                "",
                lattitude,
                longitude
            )
            complaintsPresenter.saveDetailsRequest(request)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }

    }

    override fun showEmptyLevelError() {
        dismissProgress()
        Utilities.showMessage(this,getString(R.string.select_urgency_level))
    }

    override fun showEmptyDescError() {
        dismissProgress()
        Utilities.showMessage(this,getString(R.string.please_enter_description))
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }
}