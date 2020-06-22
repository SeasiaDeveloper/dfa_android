package com.dfa.ui.ngoform.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.NGORequest
import com.dfa.pojo.response.NGOResponse
import com.dfa.ui.ngoform.presenter.NGOFormPresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class NGOFormModel(private var ngoPresenter: NGOFormPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }
    /*
    * hit api to save NGO Data
    * */
    fun sendNGORequest(ngoRequest: NGORequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(ngoRequest.complaint_id!!)
        map["comment"] = toRequestBody(ngoRequest.comment)
        retrofitApi.addNGOData(map).enqueue(object :
            Callback<NGOResponse> {
            override fun onResponse(
                call: Call<NGOResponse>,
                response: Response<NGOResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.status == 200) {
                        ngoPresenter.onSaveDetailsSuccess(responseObject)
                    } else {
                        ngoPresenter.onSaveDetailsFailed(response.body()?.message ?: Constants.SERVER_ERROR)
                    }
                } else {
                    ngoPresenter.showError(Constants.SERVER_ERROR)
                }
            }
            override fun onFailure(call: Call<NGOResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    ngoPresenter.showError("Socket Time error")
                }else{
                    ngoPresenter.showError(t.message + "")
                }
            }
        })
    }
}