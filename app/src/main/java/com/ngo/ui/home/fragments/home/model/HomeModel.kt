package com.ngo.ui.home.fragments.home.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.response.GetProfileResponse
import com.ngo.ui.home.fragments.home.presenter.HomePresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeModel(private var homePresenter: HomePresenter) {
    private lateinit var getProfileResponse: GetProfileResponse
    fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun getProfileData(token: String?) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getProfileData(token).enqueue(object :
            Callback<GetProfileResponse> {
            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                homePresenter.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<GetProfileResponse>,
                response: Response<GetProfileResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        homePresenter.getProfileSuccess(responseObject)
                    } else {
                        homePresenter.getProfileFailure(Constants.SERVER_ERROR)
                    }
                } else {
                    homePresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}