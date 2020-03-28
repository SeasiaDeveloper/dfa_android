package com.ngo.ui.home.fragments.videos.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetPhotosResponse
import com.ngo.ui.home.fragments.photos.presenter.PhotosPresenter
import com.ngo.ui.home.fragments.videos.presenter.VideoPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideosModel (private var presenter: VideoPresenter) {

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun fetchVideos(request: GetPhotosRequest) {
        val retrofitApi = ApiClient.getClientWithToken().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["type"] = toRequestBody(request.type)

        retrofitApi.getcrime_media(map).enqueue(object : Callback<GetPhotosResponse> {
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
}