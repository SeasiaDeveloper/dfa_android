package com.dfa.ui.police.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.PoliceStatusRequest
import com.dfa.pojo.response.GetPoliceFormData
import com.dfa.pojo.response.PoliceStatusResponse
import com.dfa.ui.police.presenter.PolicePresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class PoliceModel(private var policePresenter:PolicePresenter) {

    /*
    * hit api to get details from NGO
    * */

    fun getNGODetailsForPoliceRequest() {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getNgoDetailsForPolice().enqueue(object :
            Callback<GetPoliceFormData> {
            override fun onResponse(
                call: Call<GetPoliceFormData>,
                response: Response<GetPoliceFormData>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.status == 200) {
                        policePresenter.onPoliceDetailsSuccess(responseObject)
                    } else {
                        policePresenter.onPoliceDetailsFailed(response.body()?.message ?: Constants.SERVER_ERROR)
                    }
                } else {
                    policePresenter.showError(Constants.SERVER_ERROR)
                }
            }
            override fun onFailure(call: Call<GetPoliceFormData>, t: Throwable) {
                if(t is SocketTimeoutException){
                    policePresenter.showError("Socket Time error")
                }else{
                    policePresenter.showError(t.message + "")
                }
            }
        })
    }

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }
    /*
    * hit api to mark Police Status
    * */
    fun sendNGORequest(statusRequest: PoliceStatusRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["status"] = toRequestBody(statusRequest.status)
        map["forward_id"] = toRequestBody(statusRequest.forward_id)
        map["police_comment"] = toRequestBody(statusRequest.comment)

        retrofitApi.savePoliceStatus(map).enqueue(object :
            Callback<PoliceStatusResponse> {
            override fun onResponse(
                call: Call<PoliceStatusResponse>,
                response: Response<PoliceStatusResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.status == 200) {
                        policePresenter.onPoliceStatusSuccess(responseObject)
                    } else {
                        policePresenter.onPoliceStatusFailed(response.body()?.message ?: Constants.SERVER_ERROR)
                    }
                } else {
                    policePresenter.showError(Constants.SERVER_ERROR)
                }
            }
            override fun onFailure(call: Call<PoliceStatusResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    policePresenter.showError("Socket Time error")
                }else{
                    policePresenter.showError(t.message + "")
                }
            }
        })
    }
}