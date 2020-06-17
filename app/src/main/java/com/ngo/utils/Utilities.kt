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
import android.location.Address
import android.location.Geocoder
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
import com.kaopiz.kprogresshud.KProgressHUD
import com.ngo.R
import com.ngo.adapters.StatusAdapter
import com.ngo.listeners.AdharNoListener
import com.ngo.listeners.AlertDialogListener
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.listeners.StatusListener
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.utils.algo.VerhoeffAlgo
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utilities {
    private lateinit var customDialog: KProgressHUD
    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 121
    val MY_PERMISIIONS_CAMERA = 122
    val PERMISSION_ID = 44
    val PERMISSION_ID_CAMERA = 45
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

    fun checkCameraPermissions(context: Activity): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
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

    fun requestPermissionsForCamera(context: Activity) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            PERMISSION_ID_CAMERA
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

    fun calculateDistanceUsingGoogleApi(
        latitude: String?,
        longitude: String?,
        mContext: Context
    ): StringBuilder {
        val latitude1 = PreferenceHandler.readString(mContext, PreferenceHandler.LATITUDE, "")
        val longitude1 = PreferenceHandler.readString(mContext, PreferenceHandler.LONGITUDE, "")
/*        var stringBuilder = StringBuilder()
        var dist = 0.0
        try {
            //destinationAddress = destinationAddress.replaceAll(" ", "%20");
            var url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + latitude1 + "," + longitude1 + "&destination=" + latitude + "," + longitude + "&key=AIzaSyBrYlAvwOypJ4ZYp4u5DDVFvajUQVJ4xkY";
           // https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=40.6655101,-73.89188969999998&destinations=40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626&key=YOUR_API_KEY
            var httppost = HttpPost(url);

            var client = DefaultHttpClient();
            var response: HttpResponse

            response = client.execute(httppost);
            var entity = response.getEntity();
            var stream = entity.getContent();
            var b: Int
            b = stream.read()
            while (b != -1) {
                stringBuilder.append(b)
            }
        } catch (e: ClientProtocolException) {
        } catch (e: IOException) {
        }


        try {
            var jsonObject = JSONObject(stringBuilder.toString())
            var array = jsonObject.getJSONArray("routes");
            var routes = array.getJSONObject(0);
            var legs = routes.getJSONArray("legs");
            var steps = legs.getJSONObject(0);
            var distance = steps.getJSONObject("distance");
            Log.i("Distance", distance.toString());
           // dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]", ""));

        } catch (e: JSONException) {
            e.printStackTrace();
        }*/


        var mUrlConnection: HttpURLConnection? = null
        var mJsonResults = StringBuilder();
        try {
            var urlFinal =
                "http://maps.googleapis.com/maps/api/directions/json?origin=" + latitude1 + "," + longitude1 + "&destination=" + latitude + "," + longitude + "&key=AIzaSyBrYlAvwOypJ4ZYp4u5DDVFvajUQVJ4xkY";
            var sb = urlFinal
            /*sb.append("?origin=" + URLEncoder.encode("Your origin address", "utf8"));
            sb.append("&destination=" + URLEncoder.encode("Your destination address", "utf8"));
            sb.append("&key=" + "AIzaSyBrYlAvwOypJ4ZYp4u5DDVFvajUQVJ4xkY");*/

            var url = URL(sb)
            mUrlConnection = url.openConnection() as HttpURLConnection
            var inputStremReader = InputStreamReader(mUrlConnection.getInputStream())

            // Load the results into a StringBuilder
            var read: Int
            //var buff = Char[1024]
            val bufferArray = CharArray(1024)
            //var charArray = ArrayList<Char>(1024)
            // var buff = Array<Char>[1024]
            read = inputStremReader.read(bufferArray)
            while (read != -1) {
                mJsonResults.append(bufferArray, 0, read);
            }
        } catch (e: MalformedURLException) {
            System.out.println("Error processing Distance Matrix API URL");
            //return null

        } catch (e: IOException) {
            System.out.println("Error connecting to Distance Matrix");
            //return null;
        } finally {
            if (mUrlConnection != null) {
                mUrlConnection.disconnect();
            }
        }

        try {
            var jsonObject = JSONObject(mJsonResults.toString())
            var array = jsonObject.getJSONArray("routes");
            var routes = array.getJSONObject(0);
            var legs = routes.getJSONArray("legs");
            var steps = legs.getJSONObject(0);
            var distance = steps.getJSONObject("distance");
            Log.i("Distance", distance.toString());
            // dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]", ""));
        } catch (e: JSONException) {
            e.printStackTrace();
        }
        return mJsonResults;
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

    fun changeTimeFormat(date: String): String {
        val time = date.split(":") as ArrayList<String>
        val amPm = date.split(" ") as ArrayList<String>
        val formatedTime = time.get(0) + ":" + time.get(1) + " " + amPm.get(1)
        return formatedTime
    }

    fun displayDialog(
        context: Context,
        title: String?,
        message: String?,
        item: Any,
        alertDialogListener: AlertDialogListener,
        position: Int
    ) {

        val dialogBuilder = android.app.AlertDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok", { dialog, id ->

                if(title.equals("DELETE POST")){
                    alertDialogListener.onClick(item, position)
                } else{
                    alertDialogListener.onHide(item, position)
                }

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
                Toast.makeText(activity, "Aadhaar no cannot be empty", Toast.LENGTH_SHORT).show()
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

    fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun dateTime(): String {
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        return formatted
    }

    fun getAddressFromLatLong(latitude:Double,longitude:Double,context: Context):String {
        var geocoder: Geocoder
        var addresses: List<Address>
        geocoder = Geocoder (context, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        var address = addresses . get (0).getAddressLine(0)// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        var city = addresses . get (0).getLocality()
        var state = addresses . get (0).getAdminArea()
        var country = addresses . get (0).getCountryName()
        var postalCode = addresses . get (0).getPostalCode()
        var knownName = addresses . get (0).getFeatureName()
        return address;
    }

}