package com.dfa.ui.forgotpassword.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.VerifyUserRequest
import com.dfa.pojo.response.VerifyUserResponse
import com.dfa.ui.forgotpassword.presenter.ForgotPassworPresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

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
                if(t is SocketTimeoutException){
                    forgotPassworPresenter.showError("Socket Time error")
                }else{
                    forgotPassworPresenter.showError(t.message + "")
                }
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

    fun hitMobileNumberExistsApi(verifyUserRequest: VerifyUserRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["phone_number"] = toRequestBody(verifyUserRequest.phoneNumber)
        retrofitApi.verifyUser(map).enqueue(object :
            Callback<VerifyUserResponse> {
            override fun onFailure(call: Call<VerifyUserResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    forgotPassworPresenter.showError("Socket Time error")
                }else{
                    forgotPassworPresenter.showError(t.message + "")
                }
            }

            override fun onResponse(
                call: Call<VerifyUserResponse>,
                response: Response<VerifyUserResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        forgotPassworPresenter.numberExistsAlready(responseObject)
                    } else {
                        forgotPassworPresenter.numberDoesnotExist(
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