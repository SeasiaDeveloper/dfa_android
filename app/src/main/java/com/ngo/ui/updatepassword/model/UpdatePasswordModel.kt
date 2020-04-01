package com.ngo.ui.updatepassword.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.request.UpdatePasswordRequest
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.ui.updatepassword.presenter.UpdatePasswordPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePasswordModel(private var updatePasswordPresenter: UpdatePasswordPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun changePasswordApi(changePasswordRequest: UpdatePasswordRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.updatePassword(changePasswordRequest).enqueue(object :
            Callback<ChangePasswordResponse> {
            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                updatePasswordPresenter.showError(t.message + "")
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