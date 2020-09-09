package com.dfa.ui.home.fragments.home.presenter

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.application.MyApplication
import com.dfa.pojo.response.AdvertisementInput
import com.dfa.pojo.response.AdvertisementResponse
import com.dfa.ui.home.fragments.AdvertiseMentFragment
import com.dfa.ui.home.fragments.HomeFragment
import com.dfa.utils.Constants.SERVER_ERROR
import com.dfa.utils.PreferenceHandler
import com.google.android.gms.common.internal.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class AdvertisementHomePresenter(var home: HomeFragment) {

    fun getAdvertisement(input: AdvertisementInput) {
        val token =
            PreferenceHandler.readString(MyApplication.instance, PreferenceHandler.AUTHORIZATION, "")
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getDistAdvertisement(token,input).enqueue(object : Callback<AdvertisementResponse> {

            override fun onResponse(call: Call<AdvertisementResponse>, response: Response<AdvertisementResponse>) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.data !=null) {
                        home.advertisementSuccess(responseObject)
                    } else {
                        home.failer("Somthing went wrong")
                    }
                } else {
                    home.failer("Server Error")
                }
            }
            override fun onFailure(call: Call<AdvertisementResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    home.failer("Socket Time error")
                }else{
                    home.failer(t.message + "")
                }
            }
        })
    }
}