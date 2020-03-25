package com.ngo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService
import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Dialog
import android.net.ParseException
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ngo.R
import com.ngo.utils.algo.VerhoeffAlgo
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Utilities {
    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 121
    val PERMISSION_ID = 44
    private lateinit var formatedDate: String
    private lateinit var formattedTime: String

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun checkPermission(context: Context, permission : String, requestCode : Int): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        permission
                    )
                ) {
                    val alertBuilder = AlertDialog.Builder(context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("External storage permission is necessary")
                    alertBuilder.setPositiveButton(android.R.string.yes
                    ) { dialog, which ->
                        ActivityCompat.requestPermissions(
                            context,
                            arrayOf(permission),
                            requestCode
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.show()

                } else {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(permission),
                        requestCode
                    )
                }
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }
    /**
     * Show toast message
     */
    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }



    fun showAlert(activity : Activity, message1 : String) {
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
                R.layout.layout_custom_alert,
                null,
                false
            )

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setTitle(activity.getString(R.string.app_name))

        // set the custom dialog components - text, image and button
        val text = dialog.findViewById(R.id.text) as TextView
        text.text = message1

        val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    /**
     * This method used to hide keyboard
     */
    fun hideKeyboard(context: Activity) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(context.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            //Log.e("Keyboard Exception", e.message)
        }
    }
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context) {
        val androidID = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        PreferenceHandler.writeString(context, PreferenceHandler.DEVICE_ID, androidID.toString())
    }
    fun getBitmapFromUri(context: Context,uri: Uri) : Bitmap? {
        var bm: Bitmap? = null

        if (uri != null) {
            try {
                bm =
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return bm
    }
    fun rotateMyImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

     fun checkPermissions(context: Activity): Boolean {
        return if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else false
    }

     fun requestPermissions(context: Activity) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun dateFormatFromDate(time: String): String {
        val sdf = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
        val sdfs = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dt: Date?
        try {
            dt = sdf.parse(time)
            formattedTime=sdfs.format(dt!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formattedTime
    }
    fun dateFormat(date: String): String {
        val oldDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
        val newDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val convertedDate: Date?
        try {
            convertedDate = oldDateFormat.parse(date)
            formatedDate = newDateFormat.format(convertedDate!!)
        } catch (e: ParseException) {
            e.printStackTrace()

        }
        return formatedDate
    }

    fun validateAadharNumber(aadharNumber: String): Boolean {
        val aadharPattern = Pattern.compile("\\d{12}")
        var isValidAadhar = aadharPattern.matcher(aadharNumber).matches()
        if (isValidAadhar) {
            isValidAadhar = VerhoeffAlgo.validateVerhoeff(aadharNumber)
        }
        return isValidAadhar
    }
}