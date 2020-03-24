package com.ngo.ui.login.model

import android.text.TextUtils
import android.util.Patterns
import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.LoginRequest
import com.ngo.pojo.response.LoginResponse
import com.ngo.ui.login.presenter.LoginPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginModel(private var loginPresenter: LoginPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun fetchCompalints(loginrequest: LoginRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["username"] = toRequestBody(loginrequest.username)
        map["password"] =
            toRequestBody(loginrequest.password) // val profileImg = MultipartBody.Part.createFormData("image", "image", RequestBody.create(MediaType.parse(imgMediaType), complaintsRequest.image))
        retrofitApi.login(map).enqueue(object :
            Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginPresenter.showError(t.message + "")
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
        } else if (TextUtils.isEmpty(password)) {
            loginPresenter.onEmptyPassword()
        } else {
            val request = LoginRequest(
                emailId,
                password
            )
            fetchCompalints(request)
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}