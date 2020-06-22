package com.dfa.ui.updatepassword.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.UpdatePasswordRequest
import com.dfa.pojo.response.ChangePasswordResponse
import com.dfa.ui.updatepassword.presenter.UpdatePasswordPresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class UpdatePasswordModel(private var updatePasswordPresenter: UpdatePasswordPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun changePasswordApi(changePasswordRequest: UpdatePasswordRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.updatePassword(changePasswordRequest).enqueue(object :
            Callback<ChangePasswordResponse> {
            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    updatePasswordPresenter.showError("Socket Time error")
                }else{
                    updatePasswordPresenter.showError(t.message + "")
                }
            }

            override fun onResponse(
                call: Call<ChangePasswordResponse>,
                response: Response<ChangePasswordResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        updatePasswordPresenter.updatePasswordSucccess(responseObject)
                    } else {
                        updatePasswordPresenter.showError(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    updatePasswordPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }

}