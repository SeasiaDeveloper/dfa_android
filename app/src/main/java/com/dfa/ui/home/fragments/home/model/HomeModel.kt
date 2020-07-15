package com.dfa.ui.home.fragments.home.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.response.*
import com.dfa.ui.home.fragments.home.presenter.HomePresenter
import com.dfa.utils.Constants
import com.dfa.utils.Utilities
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

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

    fun logout(token: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.logout(token).enqueue(object :
            Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                homePresenter.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        homePresenter.onLogoutSuccess(responseObject)
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
                      //  homePresenter.postLocationFailure( response.body()?.message ?: Constants.SERVER_ERROR )
                    }
                } else {
                 //   homePresenter.showError(Constants.SERVER_ERROR)
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

    fun saveAdhaarNo(token: String, adhaarNo: String) {
        //hit api
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
         val map = HashMap<String, RequestBody>()
        map["adhar_number"] = toRequestBody(adhaarNo)
        retrofitApi.updateProfileWithoutImage(map, token)
            .enqueue(object : Callback<SignupResponse> {
                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        homePresenter.showError("Socket Time error")
                    } else {
                        homePresenter.showError(t.message + "")
                    }
                }

                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            homePresenter.adhaarSavedSuccess(responseObject)
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