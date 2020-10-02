package com.dfa.ui.changepassword.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.ChangePasswordRequest
import com.dfa.pojo.response.ChangePasswordResponse
import com.dfa.ui.changepassword.presenter.ChangePasswordPresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class ChangePasswordModel(private var changePasswordPresenter: ChangePasswordPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun changePasswordApi(changePasswordRequest: ChangePasswordRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.changePassword(changePasswordRequest).enqueue(object :
            Callback<ChangePasswordResponse> {
            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    changePasswordPresenter.showError("Socket Time error")
                }else{
                    changePasswordPresenter.showError("Somthing went wrong, please try again latter")
                }
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
                        changePasswordPresenter.showError(response.body()?.message ?: Constants.SERVER_ERROR)
                    }
                } else {
                    changePasswordPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}