package com.ngo.ui.forgotpassword.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.request.VerifyUserRequest
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.pojo.response.VerifyUserResponse
import com.ngo.ui.changepassword.presenter.ChangePasswordPresenter
import com.ngo.ui.forgotpassword.presenter.ForgotPassworPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordModel(private var forgotPassworPresenter: ForgotPassworPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun hitVerifyUserApi(verifyUserRequest: VerifyUserRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["phone_number"] = toRequestBody(verifyUserRequest.phoneNumber)
        retrofitApi.verifyUser(map).enqueue(object :
            Callback<VerifyUserResponse> {
            override fun onFailure(call: Call<VerifyUserResponse>, t: Throwable) {
                forgotPassworPresenter.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<VerifyUserResponse>,
                response: Response<VerifyUserResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        forgotPassworPresenter.onVerfiyUserSuccess(responseObject)
                    } else {
                        forgotPassworPresenter.onVerifyUserFailure(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    forgotPassworPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}