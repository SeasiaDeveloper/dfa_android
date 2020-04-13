package com.ngo.ui.home.fragments.home.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetProfileResponse
import com.ngo.pojo.response.PostLocationResponse
import com.ngo.pojo.response.UpdateStatusSuccess
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
                        homePresenter.getProfileFailure(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    homePresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }

    fun getPostLocationData(token:String?,latitude:String,longitude:String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)

        val map = HashMap<String, RequestBody>()
        map["latitude"] = toRequestBody(latitude)
        map["longitude"] = toRequestBody(longitude)

        retrofitApi.postLocationData(token,map).enqueue(object :
            Callback<PostLocationResponse> {
            override fun onFailure(call: Call<PostLocationResponse>, t: Throwable) {
                //homePresenter.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<PostLocationResponse>,
                response: Response<PostLocationResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        homePresenter.postLocationSuccess(responseObject)
                    } else {
                        //homePresenter.postLocationFailure( response.body()?.message ?: Constants.SERVER_ERROR )
                    }
                } else {
                   // homePresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }

    fun updateStatus(token: String, complaintId: String, statusId: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(complaintId)
        map["status"] = toRequestBody(statusId)

        retrofitApi.updateStatus(token, map)
            .enqueue(object : Callback<UpdateStatusSuccess> {
                override fun onFailure(call: Call<UpdateStatusSuccess>, t: Throwable) {
                    homePresenter.showError(t.message + "")
                }

                override fun onResponse(
                    call: Call<UpdateStatusSuccess>,
                    response: Response<UpdateStatusSuccess>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            homePresenter.statusUpdationSuccess(responseObject)
                        } else {
                            homePresenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        homePresenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }
}