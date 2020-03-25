package com.ngo.ui.changepassword.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.pojo.response.LoginResponse
import com.ngo.ui.changepassword.presenter.ChangePasswordPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordModel(private var changePasswordPresenter: ChangePasswordPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun changePasswordApi(changePasswordRequest: ChangePasswordRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
       /* val map = HashMap<String, RequestBody>()
        map["password"] = toRequestBody(changePasswordRequest.password)
        map["confirm_password"] = toRequestBody(changePasswordRequest.confirm_password)
        map["user_id"] = toRequestBody(changePasswordRequest.user_id)*/
        retrofitApi.changePassword(changePasswordRequest).enqueue(object :
            Callback<ChangePasswordResponse> {
            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                changePasswordPresenter.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<ChangePasswordResponse>,
                response: Response<ChangePasswordResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        changePasswordPresenter.onPasswordChangeSuccess(responseObject)
                    } else {
                        changePasswordPresenter.showError(Constants.SERVER_ERROR)
                    }
                } else {
                    changePasswordPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}