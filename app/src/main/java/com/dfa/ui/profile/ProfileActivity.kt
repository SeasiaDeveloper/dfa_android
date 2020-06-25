package com.dfa.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.GsonBuilder
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.pojo.request.SignupRequest
import com.dfa.pojo.response.DataBean
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.GetProfileResponse
import com.dfa.pojo.response.SignupResponse
import com.dfa.ui.profile.presenter.ProfilePresenter
import com.dfa.ui.profile.presenter.ProfilePresenterImplClass
import com.dfa.ui.profile.view.ProfileView
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
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
import kotlinx.android.synthetic.main.item_case.view.*

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
    private var userId = ""

    override fun fetchDistList(responseObject: DistResponse) {
        if (userId.equals("")) dismissProgress()

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

        if (userId.equals("")) {
            val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
            setData(value)
        } else {
            getUserProfile(userId)
        }

        // Set an on item selected listener for spinner object
        spDist.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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

    private fun getUserProfile(userId: String) {
        profilePresenter.fetchUserInfo(userId)
    }

    fun setData(value: String?) {

        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        if (jsondata != null) {
            if (jsondata.data?.profile_pic != null) {
                try {
                    Glide.with(this).load(jsondata.data?.profile_pic)
                        .into(imgProfile)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
                isVerified.setButtonDrawable(R.drawable.check)
                //isVerified.isChecked = true
            } else {
                isVerified.setButtonDrawable(R.drawable.un_check)
                //isVerified.isChecked = false
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
                etAdharNo.text.toString(),//  adhaarNo
                path, "",
                token
            )

            if (!(adhaarNo.equals("")) && prevAdhaarValue.equals("")) {
                isAdhaarNoAdded = true
            } else {
                isAdhaarNoAdded = false
            }

            profilePresenter.checkValidations(signupReq, isAdhaarNoAdded)

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
                if (imageUri != null) {
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
                            path = cursor.getString(columnIndex!!)
                        }
                        cursor.close()

                        val options1 = RequestOptions()
                            /* .centerCrop()*/
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)

                        try{
                        Glide.with(this).asBitmap().load(imageUri)
                                        .apply(options1)
                                        .into(imgProfile)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                        //imgProfile.setImageURI(imageUri)
                    } catch (e: Exception) {
                        try {
                            BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri))
                            imgProfile.setImageURI(imageUri)
                            path = getRealPathFromURI(imageUri)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Utilities.showMessage(
                                this@ProfileActivity,
                                getString(R.string.unable_image_fetching_message)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        if (contentResolver != null) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
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
        val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.PROFILE_IMAGE,
            "true"
        )
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        if (isAdhaarNoAdded) {

            jsondata.data?.adhar_number = responseObject.data.adhar_number //add adhar no
        }
        jsondata.data?.profile_pic = responseObject.data.profile_pic
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
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.BLACK)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
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

        if (intent.getStringExtra("id") != null)
            userId = intent.getStringExtra("id")

        if (intent.getStringExtra("fromWhere") != null) {
            if (intent.getStringExtra("fromWhere").equals("userProfile")) {
                (toolbarLayout as CenteredToolbar).title = getString(R.string.user_profile)
            } else {
                (toolbarLayout as CenteredToolbar).title = getString(R.string.edit_profile)
            }
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

        override fun firstNameAlphabetFailure() {
            dismissProgress()
            Utilities.showMessage(this, getString(R.string.first_name_alphabet_validation))
        }

        override fun firstNameLengthFailure() {
            dismissProgress()
            Utilities.showMessage(this, getString(R.string.fname_length_validation))
        }

        override fun middleNameAlphabetFailure() {
            dismissProgress()
            Utilities.showMessage(this, getString(R.string.mid_name_alphabet_validation))
        }

        override fun middleNameLengthFailure() {
            dismissProgress()
            Utilities.showMessage(this, getString(R.string.mid_name_length_validation))
        }

        override fun lastNameAlphabetFailure() {
            dismissProgress()
            Utilities.showMessage(this, getString(R.string.lastname_alphabets_validation))
        }

        override fun lastNameLengthFailure() {
            dismissProgress()
            Utilities.showMessage(this, getString(R.string.last_name_length_validation))
        }

        override fun addressLine1LengthFailure() {
            dismissProgress()
            Utilities.showMessage(this, getString(R.string.adress_length_validation))
        }

        override fun addressLine2LengthFailure() {
            dismissProgress()
            Utilities.showMessage(this, getString(R.string.adress_two_length_validation))
        }

        override fun pinCodeLengthFailure() {
            dismissProgress()
            Utilities.showMessage(this, getString(R.string.pin_validation))
        }

        override fun getUserProfileSuccess(responseObject: GetProfileResponse) {
            dismissProgress()
            val jsonString = GsonBuilder().create().toJson(responseObject)
            //Save that String in SharedPreferences
            setData(jsonString)
            btnUpdate.visibility = View.GONE
            btnCancel.visibility = View.GONE
            layout_profile_image.visibility = View.GONE
            //set edittexts as non editable
            etFirstName.isFocusable = false
            etFirstName.isEnabled = false
            etFirstName.isClickable = false

            etMiddleName.isFocusable = false
            etMiddleName.isEnabled = false
            etMiddleName.isClickable = false

            etLastName.isFocusable = false
            etLastName.isEnabled = false
            etLastName.isClickable = false

            spDist.visibility = View.GONE
            etDist.visibility = View.VISIBLE
            etDist.setText(responseObject.data?.district)
            etDist.isFocusable = false
            etDist.isEnabled = false
            etDist.isClickable = false

            etAddress1.isFocusable = false
            etAddress1.isEnabled = false
            etAddress1.isClickable = false

            etAddress2.isFocusable = false
            etAddress2.isEnabled = false
            etAddress2.isClickable = false

            etPinCode.isFocusable = false
            etPinCode.isEnabled = false
            etPinCode.isClickable = false

            etMobile2.isFocusable = false
            etMobile2.isEnabled = false
            etMobile2.isClickable = false

            etEmail.isFocusable = false
            etEmail.isEnabled = false
            etEmail.isClickable = false

        }


    }
