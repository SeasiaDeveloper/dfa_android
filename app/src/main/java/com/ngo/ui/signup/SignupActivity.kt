package com.ngo.ui.signup

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
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DataBean
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.signup.presenter.SignupPresenter
import com.ngo.ui.signup.presenter.SignupPresenterImplClass
import com.ngo.ui.signup.view.SignupView
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.toolbarLayout
import kotlin.collections.ArrayList
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.ngo.ui.home.fragments.home.view.HomeActivity
import com.ngo.ui.login.view.LoginActivity
import com.ngo.utils.PreferenceHandler
import kotlinx.android.synthetic.main.activity_signup.btnSubmit

class SignupActivity : BaseActivity(), SignupView {

    private var authorizationToken: String? = ""
    private lateinit var token: String

    override fun getLayout(): Int {
        return R.layout.activity_signup
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.signup)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.BLACK)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        getFirebaseToken()
        if (isInternetAvailable()) {
            showProgress()
            signupPresenter.getDist() //load Districts list
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
        /*  etMobile1.setText(intent.getStringExtra("phoneNo"))
          etMobile1.isFocusable = false
          etMobile1.isEnabled = false
          etMobile1.isClickable = false*/
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

    override fun handleKeyboard(): View {
        return signupLayout
    }

    private var signupPresenter: SignupPresenter = SignupPresenterImplClass(this)
    private var IMAGE_REQ_CODE = 101
    private var path: String = ""
    private var distId = -1
    private lateinit var distList: ArrayList<DataBean>

    fun setListeners() {
        btnCancel.setOnClickListener {
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        btnSubmit.setOnClickListener {
            val signupReq = SignupRequest(
                etMobile1.text.toString(),
                etEmail.text.toString(),
                etPassword.text.toString(),
                etFirstName.text.toString(),
                etLastName.text.toString(),
                etMiddleName.text.toString(),
                distId.toString(),
                etAddress1.text.toString(),
                etAddress2.text.toString(),
                etPinCode.text.toString(),
                etMobile2.text.toString(),
                etAdharNo.text.toString(),
                path, etConfirmPassword.text.toString(),
                token
            )

            if (isInternetAvailable()) {
                showProgress()
                signupPresenter.checkValidations(signupReq)
            } else {
                Utilities.showMessage(this, getString(R.string.no_internet_connection))
            }


        }

        imgProfile.setOnClickListener {
            val resultGallery = getMarshmallowPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
            if (resultGallery)
                galleryIntent()
        }
    }

    override fun fetchDistList(responseObject: DistResponse) {
        dismissProgress()

        setListeners()

        distList = responseObject.data

        val distValueList = ArrayList<String>()
        distValueList.add("Select your district")
        for (dist in distList) {
            distValueList.add(dist.name)
        }

        val distArray = distValueList.toArray(arrayOfNulls<String>(distValueList.size))

        // Initializing an ArrayAdapter
        val adapter = ArrayAdapter(
            this@SignupActivity, // Context
            android.R.layout.simple_spinner_item, // Layout
            distArray // Array
        )

        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Finally, data bind the spinner object with dapter
        spDist.adapter = adapter

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
                if (position == 0) {
                   distId = -1
                } else {
                    for (dist in distList) {
                        if (parent.getItemAtPosition(position).equals(dist.name)) {
                            distId = (dist.id).toInt()
                            break
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"), IMAGE_REQ_CODE)
    }

    private fun getMarshmallowPermission(permissionRequest: String, requestCode: Int): Boolean {
        return Utilities.checkPermission(
            this,
            permissionRequest,
            requestCode
        )
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
                        imgProfile.setImageURI(imageUri)
                    } catch (e: Exception) {
                        try {
                            BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri))
                            imgProfile.setImageURI(imageUri)
                            path = getRealPathFromURI(imageUri)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Utilities.showMessage(
                                this@SignupActivity,
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

    override fun onValidationSuccess(signupRequest: SignupRequest) {
        dismissProgress()

        if (isInternetAvailable()) {
            showProgress()
            authorizationToken =
                PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
            signupPresenter.saveSignUpDetails(signupRequest, authorizationToken)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
    }

    override fun showResponse(response: SignupResponse) {
        dismissProgress()
        Utilities.showMessage(this, response.message)
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.AUTHORIZATION,
            "Bearer " + response.token
        )

        PreferenceHandler.writeString(
            this,
            PreferenceHandler.USER_ROLE,
            "0"
        )

        startActivity(Intent(this@SignupActivity, HomeActivity::class.java))
        finish()
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }


    //validations:-
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

    override fun passwordEmptyValidation() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.password_empty))
    }

    override fun passwordLengthValidation() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.pass_length))
    }

    override fun confirmPasswordEmptyValidation() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.confirm_pass_empty))
    }

    override fun confirmPasswordLengthValidation() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.confirm_pass_length))
    }

    override fun confirmPasswordMismatchValidation() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.confirm_pass_mismatch_pass))
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

    override fun passwordInvalidValidation() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.password_invalid))
    }

    override fun districtValidationFailure() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.district_validation))
    }

}
