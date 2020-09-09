package com.dfa.ui.home.fragments.marketplace

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.databinding.ActivityAddBusinessBinding
import com.dfa.pojo.request.AddBusinessInput
import com.dfa.pojo.response.CategoriesListResponse
import com.dfa.utils.CheckRuntimePermissions
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_business.*
import kotlinx.android.synthetic.main.upload_document_dialog.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddBusinessActivity : BaseActivity(), AddBusinessCallback,
    AdapterView.OnItemSelectedListener {
    var binding: ActivityAddBusinessBinding? = null
    var businessId = ""
    var businessName = ""
    var categoryId = "1"
    var categoryName = ""
    var address = ""
    var pin = ""
    var contect = ""
    var mobile = ""
    var latitude = ""
    var longitude = ""
    var imagePath = ""
    var product = ""
    var comingFrom = ""
    var imageUri = ""
    var img = ""
    var persenter: AddBusinessPresenter? = null
    private val CAMERA_REQUEST = 1888
    private val RESULT_LOAD_IMAGE = 1999
    var tempPath = ""
    val PERMISSION_READ_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val REQUEST_PERMISSIONS = 1

    override fun getLayout(): Int {
        return R.layout.activity_add_business
    }

    override fun setupUI() {
        binding = viewDataBinding as ActivityAddBusinessBinding
        (toolbarLayout as CenteredToolbar).title = getString(R.string.add_business)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
         latitude = PreferenceHandler.readString(this, PreferenceHandler.LATITUDE, "")!!
         longitude = PreferenceHandler.readString(this, PreferenceHandler.LONGITUDE, "")!!
        binding!!.etLatitude.setText(latitude)
        binding!!.etLongitude.setText(longitude)

        try {
            if (intent.getStringExtra("comingFrom")!!.equals("editBusiness")) {
                businessId = intent.getStringExtra("businessId")!!
                businessName = intent.getStringExtra("businessName")!!
                categoryId = intent.getStringExtra("categoryId")!!
                categoryName = intent.getStringExtra("categoryName")!!
                address = intent.getStringExtra("address")!!
                pin = intent.getStringExtra("pin")!!
                contect = intent.getStringExtra("contect")!!
                mobile = intent.getStringExtra("mobile")!!
              //  latitude = intent.getStringExtra("latitude")!!
                //longitude = intent.getStringExtra("longitude")!!
                product = intent.getStringExtra("product")!!
                imagePath = intent.getStringExtra("imageUrl")!!
                Glide.with(this).load(imagePath).placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.user)
                    .into(binding!!.businessImage)
                editDataFill()
            }
        } catch (e: Exception) {

        }
        spinner.setOnItemSelectedListener(this);
        persenter = AddBusinessPresenter(this)
        Utilities.showProgress(this)
        persenter!!.deleteBusiness()

        binding!!.btnAdd.setOnClickListener {
            callApi()
        }

        binding!!.addImgButton.setOnClickListener {
            uploadImage()
        }

        try {
            binding!!.etProduct.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (v.id === R.id.etProduct) {
                        v.parent.requestDisallowInterceptTouchEvent(true)
                        when (event.action and MotionEvent.ACTION_MASK) {
                            MotionEvent.ACTION_UP -> v.parent
                                .requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    return false
                }
            })
        } catch (e: Exception) {
        }

    }

    fun editDataFill() {
        binding!!.etBusinesName.setText(businessName)
//        binding!!.etBusinesCategories.setText(categoryName)
        binding!!.etAddress.setText(address)
        binding!!.etPin.setText(pin)
        binding!!.etContact.setText(contect)
        binding!!.etMobile.setText(mobile)
        binding!!.etLatitude.setText(latitude)
        binding!!.etLongitude.setText(longitude)
        binding!!.etProduct.setText(product)
    }

    fun callApi() {

        if (imagePath.toString().trim().isEmpty()) {
            Utilities.showMessage(this, "Please select image")
        } else if (binding!!.etBusinesName.text.toString().trim().isEmpty()) {
            binding!!.etBusinesName.error="Enter business name"
            binding!!.etBusinesName.requestFocus()
        } else if (binding!!.etAddress.text.toString().trim().isEmpty()) {
            binding!!.etAddress.error="Enter full address"
            binding!!.etAddress.requestFocus()
        } else if (binding!!.etPin.text.toString().trim().isEmpty()) {
            binding!!.etPin.error="Enter pin code"
            binding!!.etPin.requestFocus()
        } else if (binding!!.etPin.text.toString().length<6) {
            binding!!.etPin.error="Enter valid pin code"
            binding!!.etPin.requestFocus()
        } else if (binding!!.etContact.text.toString().trim().isEmpty()) {
            binding!!.etContact.error="Enter contact person name"
            binding!!.etContact.requestFocus()
        } else if (binding!!.etMobile.text.toString().trim().isEmpty()) {
            binding!!.etMobile.error="Enter mobile number"
            binding!!.etMobile.requestFocus()
        } else if (binding!!.etProduct.text.toString().trim().isEmpty()) {
            binding!!.etProduct.error="Enter product name"
            binding!!.etProduct.requestFocus()
        } else {
            val input = AddBusinessInput()
            input.name = binding!!.etBusinesName.text.toString().trim()
            input.business_pics = imageUri
            input.category_id = categoryId
            input.address = binding!!.etAddress.text.toString().trim()
            input.product = binding!!.etProduct.text.toString().trim()
            input.pincode = binding!!.etPin.text.toString().trim()
            input.contact_person = binding!!.etContact.text.toString().trim()
            input.mobile = binding!!.etMobile.text.toString().trim()
            input.latitude = binding!!.etLatitude.text.toString().trim().trim()
            input.longitude = binding!!.etLongitude.text.toString()
            input.businessId = businessId
            Utilities.showProgress(this)
            persenter!!.addBusiness(input)
        }
    }

    var categoryList: ArrayList<CategoriesListResponse.Data>? = null

    fun CategorySpinnerData(categoryList: ArrayList<CategoriesListResponse.Data>) {
        var acaegotyName = ArrayList<String>()
        for (i in 0..categoryList.size - 1) {
            acaegotyName.add(categoryList.get(i).name!!)
        }

        val dataAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, acaegotyName)
        spinner.setAdapter(dataAdapter);


        if (!categoryId.isEmpty()) {

            for (i in 0..categoryList.size - 1) {
                if (categoryList.get(i).id!!.contains(categoryId)) {
                    spinner.setSelection(i)
                }
            }
        }
    }

    override fun handleKeyboard(): View {
        return parentView
    }

    override fun onSuccess(message: String?) {
        Utilities.dismissProgress()
        Toast.makeText(this, "" + message, Toast.LENGTH_LONG).show()


        val data =  Intent();
        data.putExtra("data","12")
        setResult(RESULT_OK, data);
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun getCategories(responseObject: CategoriesListResponse) {
        categoryList = ArrayList()
        categoryList = responseObject.data!!
        CategorySpinnerData(categoryList!!)
        Utilities.dismissProgress()
    }

    override fun onError(err: String) {
        Utilities.dismissProgress()
        Toast.makeText(this, "" + err, Toast.LENGTH_LONG).show()
    }


    private fun uploadImage() {

        val uploadImage = Dialog(this, R.style.Theme_Dialog);
        uploadImage.requestWindowFeature(Window.FEATURE_NO_TITLE)
        uploadImage.setContentView(R.layout.upload_document_dialog)
        uploadImage.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        uploadImage.setCancelable(true)
        uploadImage.setCanceledOnTouchOutside(true)
        uploadImage.window!!.setGravity(Gravity.BOTTOM)
        uploadImage.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        uploadImage.tvGallery.setOnClickListener {
            uploadImage.dismiss()
            gallery()
        }
        uploadImage.tvCamera.setOnClickListener {
            uploadImage.dismiss()
            camera()
        }
        uploadImage.tv_cancel.setOnClickListener {
            uploadImage.dismiss()
        }
        uploadImage.show()

    }

    fun camera() {

        if (CheckRuntimePermissions.checkMashMallowPermissions(
                this,
                PERMISSION_READ_STORAGE, REQUEST_PERMISSIONS
            )
        ) {
            try {

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (cameraIntent.resolveActivity(this!!.packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                    }
                    if (photoFile != null) {
                        val builder = StrictMode.VmPolicy.Builder()
                        StrictMode.setVmPolicy(builder.build())
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                        startActivityForResult(cameraIntent, CAMERA_REQUEST)
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        val image = File.createTempFile(
            imageFileName, // prefix
            ".jpg", // suffix
            storageDir      // directory
        )

        // Save a file: path for use with ACTION_VIEW intents
        tempPath = "file:" + image.absolutePath
        return image
    }

    fun gallery() {
        val intent = Intent()
        intent.setTypeAndNormalize("image/*")
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(
            Intent.createChooser(intent, "Select Video"),
            RESULT_LOAD_IMAGE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === CAMERA_REQUEST && resultCode === Activity.RESULT_OK) {

            CropImage.activity(Uri.parse(tempPath))
                .start(this);

        } else if (requestCode === RESULT_LOAD_IMAGE && resultCode === Activity.RESULT_OK && null != android.R.attr.data) {
            val selectedImage: Uri = data!!.getData()!!


            CropImage.activity(selectedImage)
                .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) {
                val resultUri = result.uri
                imageUri = getAbsolutePath(this@AddBusinessActivity, resultUri)!!
                //val file = File(fileUri)
                imagePath = imageUri
                Glide.with(this).load(resultUri).placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(binding!!.businessImage)
            }

        }
    }

    fun getAbsolutePath(
        activity: Context,
        uri: Uri
    ): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor? = null
            try {
                cursor = activity.contentResolver.query(uri, projection, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) { // Eat it
                e.printStackTrace()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        // val item: String = parent!!.getItemAtPosition(position).toString()
        categoryId = categoryList!!.get(position).id!!
    }
}