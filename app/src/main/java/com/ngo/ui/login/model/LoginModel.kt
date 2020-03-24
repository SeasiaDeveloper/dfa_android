package com.ngo.ui.login.model

import android.text.TextUtils
import android.widget.Toast
import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.ui.login.presenter.LoginPresenter
import com.ngo.utils.Constants
import kotlinx.android.synthetic.main.activity_login_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginModel(private var loginPresenter: LoginPresenter) {

  /*  fun fetchCompalints() {
        val service = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val response = service.getcomplains()
        response.enqueue(object : Callback<GetComplaintsResponse> {
            override fun onResponse(call: Call<GetComplaintsResponse>, response: Response<GetComplaintsResponse>) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.status == 200) {
                        loginPresenter.onLoginSuccess(responseObject)
                    } else {
                        loginPresenter.onLoginFailure(response.body()?.message ?: Constants.SERVER_ERROR)
                    }
                } else {
                    loginPresenter.showError(Constants.SERVER_ERROR)
                }
            }


            override fun onFailure(call: Call<GetComplaintsResponse>, t: Throwable) {
                presenter.showError(t.message+"")
            }
        })
    }

    fun checkValidations(emailId:String,password:String)
    {
        if (TextUtils.isEmpty(email_mobile_number.text.toString())) {
            Toast.makeText(
                this@LoginActivity,
                "Enter Email Id or Mobile Number",
                Toast.LENGTH_SHORT
            ).show()
        }
        *//*    else if (!isValidEmail(email_mobile_number.text.toString())) {
                Toast.makeText(this@LoginActivity, "Enter ", Toast.LENGTH_SHORT).show()
            } *//*
        else if (TextUtils.isEmpty(editPassword.text.toString())) {
            Toast.makeText(this@LoginActivity, "Enter Password First", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this@LoginActivity, "Login successfully", Toast.LENGTH_SHORT)
                .show()
        }
    }*/
}