package com.ngo.ui.crimedetails.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.ui.crimedetails.presenter.CrimeDetailsPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrimeDetailsModel(private var crimeDetailsPresenter: CrimeDetailsPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun getCrimeComplaints(token: String?, complaintId: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(complaintId)
        retrofitApi.getCrimeDetails(token, map).enqueue(object :
            Callback<GetCrimeDetailsResponse> {
            override fun onFailure(call: Call<GetCrimeDetailsResponse>, t: Throwable) {
                crimeDetailsPresenter.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<GetCrimeDetailsResponse>,
                response: Response<GetCrimeDetailsResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    crimeDetailsPresenter.crimeDetailsSuccess(responseObject)
                } else {
                    crimeDetailsPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}

