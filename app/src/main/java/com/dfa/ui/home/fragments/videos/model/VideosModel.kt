package com.dfa.ui.home.fragments.videos.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.request.GetPhotosRequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetPhotosResponse
import com.dfa.ui.home.fragments.videos.presenter.VideoPresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideosModel(private var presenter: VideoPresenter) {

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun fetchVideos(token: String?, request: GetPhotosRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["type"] = toRequestBody(request.type)
        if(token!!.isEmpty()){
            map["guest_user"] = toRequestBody("1")
        }

        retrofitApi.getcrime_media(token, map).enqueue(object : Callback<GetPhotosResponse> {
            override fun onResponse(
                call: Call<GetPhotosResponse>,
                response: Response<GetPhotosResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        presenter.onGetVideosSuccess(responseObject)
                    } else {
                        presenter.onGetVideosFailed(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    presenter.showError(Constants.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<GetPhotosResponse>, t: Throwable) {
                presenter.showError(t.message + "")
            }
        })
    }

    fun getCrimeComplaints(token: String?, crimeDetailsRequest: CrimeDetailsRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(crimeDetailsRequest.complaintId)
        retrofitApi.getCrimeDetails(token, map).enqueue(object :
            Callback<GetCrimeDetailsResponse> {
            override fun onFailure(call: Call<GetCrimeDetailsResponse>, t: Throwable) {
                presenter.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<GetCrimeDetailsResponse>,
                response: Response<GetCrimeDetailsResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        presenter.getCrimeDetailsSuccess(responseObject)
                    } else {
                        presenter.showError(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    presenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}