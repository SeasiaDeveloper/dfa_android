package com.ngo.ui.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
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
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.ngo.ui.OtpVerification.view.OtpVerificationActivity
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_public.*
import kotlinx.android.synthetic.main.activity_signup.btnSubmit

class SignupActivity : BaseActivity(), SignupView {

    override fun getLayout(): Int {
        return R.layout.activity_signup
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.signup)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)

        if (isInternetAvailable()) {
            showProgress()
            signupPresenter.getDist() //load Districts list
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
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
                path, etConfirmPassword.text.toString()
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

    override fun onValidationSuccess(signupRequest: SignupRequest) {
        dismissProgress()

        if (isInternetAvailable()) {
            showProgress()
            signupPresenter.saveSignUpDetails(signupRequest)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
    }

    override fun showResponse(response: SignupResponse) {
        dismissProgress()
        Utilities.showMessage(this, response.message)
        val mobile: String = response.data.mobile
        if (!mobile.isNullOrEmpty()) {
            var intent = Intent(this, OtpVerificationActivity::class.java)
            intent.putExtra("mobile", mobile)
            intent.putExtra("intent_from", "SignUp")
            startActivity(intent)
        }
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
}
