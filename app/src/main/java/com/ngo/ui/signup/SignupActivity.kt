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
import androidx.appcompat.app.AppCompatActivity
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.signup.presenter.SignupPresenter
import com.ngo.ui.signup.presenter.SignupPresenterImplClass
import com.ngo.ui.signup.view.SignupView
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.toolbarLayout

class SignupActivity : AppCompatActivity(), SignupView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)
        (toolbarLayout as CenteredToolbar).title = getString(R.string.signup)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        setListeners()
    }

    private var signupPresenter: SignupPresenter = SignupPresenterImplClass(this)
    private var IMAGE_REQ_CODE = 101
    private var path: String = ""

    fun setListeners() {
        btnSubmit.setOnClickListener {
            val signupReq = SignupRequest(
                etMobile1.text.toString(),
                etEmail.text.toString(),
                etPassword.text.toString(),
                etFirstName.text.toString(),
                etLastName.text.toString(),
                etMiddleName.text.toString(),
                etDist.text.toString(),
                etAddress1.text.toString(),
                etAddress2.text.toString(),
                etPinCode.text.toString(),
                etMobile2.text.toString(),
                etAdharNo.text.toString(),
                path
            )

            signupPresenter.checkValidations(signupReq)

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
        // dismissProgress()
        /*  if (isInternetAvailable()) {
              showProgress()*/


        signupPresenter.saveSignUpDetails(signupRequest)
        /* } else {
             Utilities.showMessage(this, getString(R.string.no_internet_connection))
         }*/
    }

    override fun showResponse(response: SignupResponse) {
        // dismissProgress()
        Utilities.showMessage(this, response.message)
        // startActivity(Intent(this,))
        finish()
    }

    override fun showServerError(error: String) {
        //dismissProgress()
        Utilities.showMessage(this, error)
    }
}
