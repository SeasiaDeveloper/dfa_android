package com.ngo.ui.emergency.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.EmergencyDataRequest
import com.ngo.pojo.response.EmergencyDataResponse
import com.ngo.ui.emergency.presenter.EmergencyFragmentPresenter
import com.ngo.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class EmergencyFragmentModel(private var emergencyFragmentPresenter: EmergencyFragmentPresenter) {

    fun hitEmergencyApi(request: EmergencyDataRequest, token: String?) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getEmergencyData(token, request.distId!!).enqueue(object :
            Callback<EmergencyDataResponse> {
            override fun onFailure(call: Call<EmergencyDataResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    emergencyFragmentPresenter.showError("Socket Time error")
                } else {
                    emergencyFragmentPresenter.showError(t.message + "")
                }
            }

            override fun onResponse(
                call: Call<EmergencyDataResponse>,
                response: Response<EmergencyDataResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        emergencyFragmentPresenter.emergencySuccess(responseObject)
                    } else {
                        emergencyFragmentPresenter.emergencyFailure(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    emergencyFragmentPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}