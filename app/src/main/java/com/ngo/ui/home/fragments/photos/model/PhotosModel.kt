package com.ngo.ui.home.fragments.photos.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetPhotosResponse
import com.ngo.ui.home.fragments.photos.presenter.PhotosPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhotosModel(private var presenter: PhotosPresenter) {

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun fetchPhotos(token: String?, request: GetPhotosRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["type"] = toRequestBody(request.type)

        retrofitApi.getcrime_media(token, map).enqueue(object : Callback<GetPhotosResponse> {
            override fun onResponse(
                call: Call<GetPhotosResponse>,
                response: Response<GetPhotosResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        presenter.onGetPhotosSuccess(responseObject)
                    } else {
                        presenter.onGetPhotosFailed(
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