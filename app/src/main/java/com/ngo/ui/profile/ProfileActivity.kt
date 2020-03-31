package com.ngo.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DataBean
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.GetProfileResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.profile.presenter.ProfilePresenter
import com.ngo.ui.profile.presenter.ProfilePresenterImplClass
import com.ngo.ui.profile.view.ProfileView
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.btnCancel
import kotlinx.android.synthetic.main.activity_profile.etAddress1
import kotlinx.android.synthetic.main.activity_profile.etAddress2
import kotlinx.android.synthetic.main.activity_profile.etAdharNo
import kotlinx.android.synthetic.main.activity_profile.etEmail
import kotlinx.android.synthetic.main.activity_profile.etFirstName
import kotlinx.android.synthetic.main.activity_profile.etLastName
import kotlinx.android.synthetic.main.activity_profile.etMiddleName
import kotlinx.android.synthetic.main.activity_profile.etMobile1
import kotlinx.android.synthetic.main.activity_profile.etMobile2
import kotlinx.android.synthetic.main.activity_profile.etPinCode
import kotlinx.android.synthetic.main.activity_profile.imgProfile
import kotlinx.android.synthetic.main.activity_profile.spDist
import kotlinx.android.synthetic.main.activity_profile.toolbarLayout
import kotlinx.android.synthetic.main.activity_signup.*

class ProfileActivity : BaseActivity(), ProfileView {

    private var distId = -1
    private lateinit var distList: ArrayList<DataBean>
    private var path: String = ""
    private var profilePresenter: ProfilePresenter = ProfilePresenterImplClass(this)
    private var IMAGE_REQ_CODE = 101
    private lateinit var token: String
    private var authorizationToken: String? = ""
    private var adhaarNo = ""
    private var isAdhaarNoAdded = false
    private lateinit var prevAdhaarValue: String

    override fun fetchDistList(responseObject: DistResponse) {
        dismissProgress()

        setListeners()

        distList = responseObject.data
        val distValueList = ArrayList<String>()
        for (dist in distList) {

            distValueList.add(dist.name)
        }

        val distArray = distValueList.toArray(arrayOfNulls<String>(distValueList.size))

        // Initializing an ArrayAdapter
        val adapter = ArrayAdapter(
            this@ProfileActivity, // Context
            android.R.layout.simple_spinner_item, // Layout
            distArray // Array
        )
        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Finally, data bind the spinner object with dapter
        spDist.adapter = adapter
        setData()

        // Set an on item selected listener for spinner object
        spDist.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // Display the selected item text on text view
                "Spinner selected : ${parent.getItemAtPosition(position)}"

                for (dist in distList) {
                    if (parent.getItemAtPosition(position).equals(dist.name)) {
                        distId = (dist.id).toInt()
                        break
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

    fun setData() {
        //  var data = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
        val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        if (jsondata != null) {
            if (jsondata.data?.profile_pic != null) {
                Glide.with(this).load(jsondata.data?.profile_pic)
                    .into(imgProfile)
            }
            //path=jsondata.data?.profile_pic!!
            etAddress1.setText(jsondata.data?.address_1)
            etAddress2.setText(jsondata.data?.address_2)
            etMobile1.setText(jsondata.data?.username)
            etFirstName.setText(jsondata.data?.first_name)
            etMiddleName.setText(jsondata.data?.middle_name)
            etLastName.setText(jsondata.data?.last_name)

            for (dist in distList) {
                if (dist.name.equals(jsondata.data?.district)) {
                    distId = (dist.id).toInt()
                    spDist.setSelection(distId - 1)
                    break
                }
            }
            etPinCode.setText(jsondata.data?.pin_code)
            etMobile2.setText(jsondata.data?.mobile)
            etEmail.setText(jsondata.data?.email)
            if (jsondata.data?.isVerified.equals("1")) {
                isVerified.isChecked = true
            } else {
                isVerified.isChecked = false
            }

            if (jsondata.data?.adhar_number != null) {
                prevAdhaarValue = jsondata.data?.adhar_number!!
                adhaarNo = jsondata.data?.adhar_number!!
            }

            //  if(jsondata.data?.adhar_number!! != null)
            if (!adhaarNo.equals("")) {
                etAdharNo.setText(jsondata.data?.adhar_number)
                etAdharNo.isFocusable = false
                etAdharNo.isEnabled = false
                etAdharNo.isClickable = false
                isAdhaarNoAdded = false
            } else {
                etAdharNo.isFocusable = true
                etAdharNo.isEnabled = true
                etAdharNo.isClickable = true
            }

            etMobile1.isFocusable = false
            etMobile1.isEnabled = false
            etMobile1.isClickable = false
        }
    }

    private fun getFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                token = task.result?.token.toString()

                // Log and toast

            })
    }

    fun setListeners() {
        btnCancel.setOnClickListener {
            onBackPressed()
        }
        btnUpdate.setOnClickListener {
            adhaarNo = etAdharNo.text.toString()
            val signupReq = SignupRequest(
                etMobile1.text.toString(),
                etEmail.text.toString(),
                "",
                etFirstName.text.toString(),
                etLastName.text.toString(),
                etMiddleName.text.toString(),
                distId.toString(),
                etAddress1.text.toString(),
                etAddress2.text.toString(),
                etPinCode.text.toString(),
                etMobile2.text.toString(),
                adhaarNo,
                path, "",
                token
            )

            if (!(adhaarNo.equals("")) && prevAdhaarValue.equals("")) {
                isAdhaarNoAdded = true
            } else {
                isAdhaarNoAdded = false
            }

            if (isInternetAvailable()) {
                showProgress()
                profilePresenter.checkValidations(signupReq)
            } else {
                Utilities.showMessage(this, getString(R.string.no_internet_connection))
            }
        }

        layout_profile_image.setOnClickListener {
            val resultGallery = getMarshmallowPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
            if (resultGallery)
                galleryIntent()
        }
    }

    private fun getMarshmallowPermission(permissionRequest: String, requestCode: Int): Boolean {
        return Utilities.checkPermission(
            this,
            permissionRequest,
            requestCode
        )
    }

    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"), IMAGE_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQ_CODE && resultCode == Activity.RESULT_OK && null != data) {
            if (data.data != null) {
                val imageUri = data.data
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
                    path = cursor.getString(columnIndex!!)
                }
                cursor.close()
                imgProfile.setImageURI(imageUri)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent()
            }
        }

    }


    override fun onSuccessfulUpdation(responseObject: SignupResponse) {
        dismissProgress()
        Utilities.showMessage(this, responseObject.message)
        if (isAdhaarNoAdded) {
            val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
            val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
            jsondata.data?.adhar_number = responseObject.data.adhar_number //add adhar no
        }
        finish()
    }

    override fun onValidationSuccess(request: SignupRequest) {
        dismissProgress()

        if (isInternetAvailable()) {
            showProgress()
            authorizationToken =
                PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
            profilePresenter.updateProfile(request, authorizationToken, isAdhaarNoAdded)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_profile
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.edit_profile)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        getFirebaseToken()
        if (isInternetAvailable()) {
            showProgress()
            profilePresenter.getDist() //load Districts list
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
    }

    override fun handleKeyboard(): View {
        return profileLayout
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun usernameEmptyValidation() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.fill_mobile))
    }

    override fun usernameValidationFailure() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.valid_mobile_number))
    }

    override fun firstNameValidationFailure() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.valid_first_name))
    }

    override fun lastNameValidationFailure() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.valid_last_name))
    }

    override fun Address1ValidationFailure() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.valid_address_1))
    }

    override fun pinCodeValidationFailure() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.valid_pin_code))
    }

    override fun mobileValidationFailure() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.valid_mobile))
    }

    override fun adhaarNoValidationFailure() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.valid_adhar))
    }

    override fun emailValidationFailure() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.valid_email))
    }

}
