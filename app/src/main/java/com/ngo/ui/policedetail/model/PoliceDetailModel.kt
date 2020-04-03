package com.ngo.ui.policedetail.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.request.PoliceDetailrequest
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.ui.crimedetails.presenter.CrimeDetailsPresenter
import com.ngo.ui.policedetail.presenter.PoliceDetailPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PoliceDetailModel(private var policeDetailPresenter: PoliceDetailPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun getCrimeComplaints(token: String?, crimeDetailsRequest: PoliceDetailrequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(crimeDetailsRequest.complaintId)
        retrofitApi.getCrimeDetails(token, map).enqueue(object :
            Callback<GetCrimeDetailsResponse> {
            override fun onFailure(call: Call<GetCrimeDetailsResponse>, t: Throwable) {
                policeDetailPresenter.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<GetCrimeDetailsResponse>,
                response: Response<GetCrimeDetailsResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        policeDetailPresenter.crimeDetailsSuccess(responseObject)
                    } else {
                        policeDetailPresenter.showError(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    policeDetailPresenter.showError(Constants.SERVER_ERROR)
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
                    policeDetailPresenter.showError(t.message + "")
                }

                override fun onResponse(
                    call: Call<GetStatusResponse>,
                    response: Response<GetStatusResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            policeDetailPresenter.onListFetchedSuccess(responseObject)
                        } else {
                            policeDetailPresenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        policeDetailPresenter.showError(Constants.SERVER_ERROR)
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
            .enqueue(object : Callback<DeleteComplaintResponse> {
                override fun onFailure(call: Call<DeleteComplaintResponse>, t: Throwable) {
                    policeDetailPresenter.showError(t.message + "")
                }

                override fun onResponse(
                    call: Call<DeleteComplaintResponse>,
                    response: Response<DeleteComplaintResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            policeDetailPresenter.statusUpdationSuccess(responseObject)
                        } else {
                            policeDetailPresenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        policeDetailPresenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }

}