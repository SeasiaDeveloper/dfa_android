package com.dfa.ui.crimedetails.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetStatusResponse
import com.dfa.pojo.response.UpdateStatusSuccess
import com.dfa.ui.crimedetails.presenter.CrimeDetailsPresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class CrimeDetailsModel(private var crimeDetailsPresenter: CrimeDetailsPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun getCrimeComplaints(token: String?, crimeDetailsRequest: CrimeDetailsRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(crimeDetailsRequest.complaintId)
        retrofitApi.getCrimeDetails(token,
            map).enqueue(object :
            Callback<GetCrimeDetailsResponse> {
            override fun onFailure(call: Call<GetCrimeDetailsResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    crimeDetailsPresenter.showError("Socket Time error")
                }else{
                    crimeDetailsPresenter.showError(t.message + "")
                }
            }

            override fun onResponse(
                call: Call<GetCrimeDetailsResponse>,
                response: Response<GetCrimeDetailsResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        crimeDetailsPresenter.crimeDetailsSuccess(responseObject)
                    } else {
                        crimeDetailsPresenter.showError(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    crimeDetailsPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }

    fun fetchStatusList(token: String, userRole: String) {
        //hit api to fetch the status
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getStatus(token, userRole.toInt())
            .enqueue(object : Callback<GetStatusResponse> {
                override fun onFailure(call: Call<GetStatusResponse>, t: Throwable) {
                    if(t is SocketTimeoutException){
                        crimeDetailsPresenter.showError("Socket Time error")
                    }else{
                        crimeDetailsPresenter.showError(t.message + "")
                    }
                }

                override fun onResponse(
                    call: Call<GetStatusResponse>,
                    response: Response<GetStatusResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            crimeDetailsPresenter.onListFetchedSuccess(responseObject)
                        } else {
                            crimeDetailsPresenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        crimeDetailsPresenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }

    fun updateStatus(token: String, complaintId: String, statusId: String, comment: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(complaintId)
        map["status"] = toRequestBody(statusId)
        if(comment.isNotEmpty()) map["comment"] = toRequestBody(comment)
        retrofitApi.updateStatus(token, map)
            .enqueue(object : Callback<UpdateStatusSuccess> {
                override fun onFailure(call: Call<UpdateStatusSuccess>, t: Throwable) {
                    if(t is SocketTimeoutException){
                        crimeDetailsPresenter.showError("Socket Time error")
                    }else{
                        crimeDetailsPresenter.showError(t.message + "")
                    }
                }

                override fun onResponse(
                    call: Call<UpdateStatusSuccess>,
                    response: Response<UpdateStatusSuccess>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            crimeDetailsPresenter.statusUpdationSuccess(responseObject,response.body()?.data!!.get(0))
                        } else {
                            crimeDetailsPresenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        crimeDetailsPresenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }
}

