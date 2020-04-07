package com.ngo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Location
import android.net.ParseException
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.kaopiz.kprogresshud.KProgressHUD
import com.ngo.R
import com.ngo.adapters.StatusAdapter
import com.ngo.listeners.AdharNoListener
import com.ngo.listeners.AlertDialogListener
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.listeners.StatusListener
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.utils.algo.VerhoeffAlgo
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Utilities {
    private lateinit var customDialog: KProgressHUD
    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 121
    val PERMISSION_ID = 44
    private lateinit var formatedDate: String
    private lateinit var formattedTime: String

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun checkPermission(context: Context, permission: String, requestCode: Int): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        permission
                    )
                ) {
                    val alertBuilder = AlertDialog.Builder(context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("External storage permission is necessary")
                    alertBuilder.setPositiveButton(
                        android.R.string.yes
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


    fun showAlert(activity: Activity, message1: String) {
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

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
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
            formattedTime = sdfs.format(dt!!)
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

    fun calculateDistance(latitude: String?, longitude: String?, mContext: Context): Int {
        val latitude1 = PreferenceHandler.readString(mContext, PreferenceHandler.LATITUDE, "")
        val longitude1 = PreferenceHandler.readString(mContext, PreferenceHandler.LONGITUDE, "")
        val locationA = Location("point A")
        if (!(latitude1.equals("")) && !(longitude1.equals(""))) {
            locationA.latitude = latitude1!!.toDouble()
            locationA.longitude = longitude1!!.toDouble()

            val locationB = Location("point B")
            locationB.latitude = latitude!!.toDouble()
            locationB.longitude = longitude!!.toDouble()
            return (locationA.distanceTo(locationB) / 1000).toInt()
        } else {
            return -1
        }
    }

    fun calculateAccurateDistance(
        latitude: String?,
        longitude: String?,
        mContext: Context
    ): Double {
        val latitude1 = PreferenceHandler.readString(mContext, PreferenceHandler.LATITUDE, "")
        val longitude1 = PreferenceHandler.readString(mContext, PreferenceHandler.LONGITUDE, "")
        /*val results = FloatArray(1)
       Location.distanceBetween(
           latitude1!!.toDouble(), longitude1!!.toDouble(),
           latitude!!.toDouble(),longitude!!.toDouble(), results
       )*/

        val Radius = 6371 // radius of earth in Km

        val lat1: Double = latitude1!!.toDouble()
        val lat2: Double = latitude!!.toDouble()
        val lon1: Double = longitude1!!.toDouble()
        val lon2: Double = longitude!!.toDouble()
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )

        return Radius * c
    }

    fun String.intOrString(latitude: String?): Any {
        val v = toIntOrNull()
        return when (v) {
            null -> this
            else -> v
        }
    }


    fun isValidMobile(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }

    fun isValidMail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /*
    * method to show show progress
    * */
    fun showProgress(context: Context) {
        dismissProgress()
        customDialog = KProgressHUD(context)
        customDialog
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.20f)
            .show()
    }

    /**
     * Dismiss the progress dialog
     */
    fun dismissProgress() {
        try {
            if (customDialog.isShowing) {
                customDialog.dismiss()
            }
        } catch (e: Exception) {
            Log.e("", e.message!!)
        }
    }

    fun changeDateFormat(date: String): String {
        val oldDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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

    fun displayDialog(
        context: Context,
        title: String?,
        message: String?,
        item: Any,
        alertDialogListener: AlertDialogListener
    ) {

        val dialogBuilder = android.app.AlertDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok", { dialog, id ->
                alertDialogListener.onClick(item)

            })
            .setNegativeButton("Cancel", { dialog, id ->
                dialog.dismiss()

            })
        val alert = dialogBuilder.create()
        alert.show()

    }

    fun displayInputDialog(activity: Context, listener: AdharNoListener) {
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
                R.layout.layout_input_alert,
                null,
                false
            )

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setTitle(activity.getString(R.string.app_name))

        // set the custom dialog components - text, image and button
        val edt = dialog.findViewById(R.id.edt) as EditText

        val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener {
            if (edt.text.toString().equals("")) {
                Toast.makeText(activity, "Adhaar no cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                listener.adharNoListener(edt.text.toString())
                dialog.dismiss()
            }
        }
        dialog.show()


    }

    fun showStatusDialog(
        description: String,
        responseObject: GetStatusResponse,
        context: Context,
        listener: OnCaseItemClickListener,
        statusListener: StatusListener
    ) {
        lateinit var dialog: android.app.AlertDialog
        val builder = android.app.AlertDialog.Builder(context)
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_change_status,
            null,
            false
        ) as com.ngo.databinding.DialogChangeStatusBinding
        // Inflate and set the layout for the dialog
        if (!description.equals("null") && !description.equals("")) binding.etDescription.setText(
            description
        )

        //display the list on the screen
        val statusAdapter = StatusAdapter(context, responseObject.data.toMutableList(), listener)
        val horizontalLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvStatus?.layoutManager = horizontalLayoutManager
        binding.rvStatus?.adapter = statusAdapter
        binding.btnDone.setOnClickListener {
            showProgress(context)
            statusListener.onStatusSelected(binding.etDescription.text.toString())
            dialog.dismiss()
        }

        builder.setView(binding.root)
        dialog = builder.create()
        dialog.show()
    }

    fun isAlphabets(name: String): Boolean {
        return name.matches("[a-zA-Z]+".toRegex())
    }

}