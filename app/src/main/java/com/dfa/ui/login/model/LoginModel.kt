package com.dfa.ui.login.model

import android.text.TextUtils
import android.util.Patterns
import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.LoginRequest
import com.dfa.pojo.response.LoginResponse
import com.dfa.ui.login.presenter.LoginPresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class LoginModel(private var loginPresenter: LoginPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun hitLoginWebService(loginrequest: LoginRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["username"] = toRequestBody(loginrequest.username)
        map["password"] = toRequestBody(loginrequest.password)
        map["device_token"]=toRequestBody(loginrequest.device_token)
        // val profileImg = MultipartBody.Part.createFormData("image", "image", RequestBody.create(MediaType.parse(imgMediaType), complaintsRequest.image))
        retrofitApi.login(map).enqueue(object :
            Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    loginPresenter.showError("Socket Time error")
                }else{
                    loginPresenter.showError("Somthing went wrong, please try again latter")
                }
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseObject = response.body()
                if (responseObject != null) {
                    // if (responseObject.status == 200) {
                    loginPresenter.onLoginSuccess(responseObject)
                    //  } else {
                    //  loginPresenter.onLoginFailure(/*response.body()?.message ?:*/ Constants.SERVER_ERROR)
                    //  }
                } else {
                    loginPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }

    fun checkValidations(emailId: String, password: String) {
        if (TextUtils.isEmpty(emailId)) {
            loginPresenter.onEmptyEmailId()
        } else if (TextUtils.isDigitsOnly(emailId) && emailId.length != 10) {
            loginPresenter.onInvalidNumber()
        }
        else if (!TextUtils.isDigitsOnly(emailId) && !isValidEmail(emailId)) {
            loginPresenter.onInvalidEmail()
        }
        else if (TextUtils.isEmpty(password)) {
            loginPresenter.onEmptyPassword()
        } else {
            loginPresenter.onValidationSuccess()
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}