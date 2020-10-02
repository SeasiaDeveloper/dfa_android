package com.dfa.ui.home.fragments.marketplace

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.application.MyApplication
import com.dfa.pojo.request.DeleteBusinessInput
import com.dfa.pojo.request.MarketPlaceInput
import com.dfa.pojo.response.DeleteBusinessResponse
import com.dfa.pojo.response.MarketPlaceResponse
import com.dfa.utils.PreferenceHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class MarketPlacePresenter(var marketPlaceFragment: MarketPlaceFragment) {
    fun getMarketPlaceData(input: MarketPlaceInput) {
        val token =
            PreferenceHandler.readString(
                MyApplication.instance,
                PreferenceHandler.AUTHORIZATION,
                ""
            )
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getMarketPlace(token, input).enqueue(object :
            Callback<MarketPlaceResponse> {

            override fun onResponse(
                call: Call<MarketPlaceResponse>,
                response: Response<MarketPlaceResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.data != null) {
                        marketPlaceFragment.onSuccess(responseObject)
                    } else {
                        marketPlaceFragment.onFailer("Somthing went wrong")
                    }
                } else {
                    marketPlaceFragment.onFailer("Server Error")
                }
            }

            override fun onFailure(call: Call<MarketPlaceResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    marketPlaceFragment.onFailer("Socket Time error")
                } else {
                    marketPlaceFragment.onFailer("Somthing went wrong, please try again latter")
                }
            }
        })
    }



    fun deleteBusiness(input: DeleteBusinessInput) {
        val token =
            PreferenceHandler.readString(
                MyApplication.instance,
                PreferenceHandler.AUTHORIZATION,
                ""
            )
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.deleteBusiness(token, input).enqueue(object :
            Callback<DeleteBusinessResponse> {

            override fun onResponse(
                call: Call<DeleteBusinessResponse>,
                response: Response<DeleteBusinessResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code==200) {
                        marketPlaceFragment.onDeleteBusiness(responseObject.message!!)
                    } else {
                        marketPlaceFragment.onFailer("Somthing went wrong")
                    }
                } else {
                    marketPlaceFragment.onFailer("Server Error")
                }
            }

            override fun onFailure(call: Call<DeleteBusinessResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    marketPlaceFragment.onFailer("Socket Time error")
                } else {
                    marketPlaceFragment.onFailer("Somthing went wrong, please try again latter")
                }
            }
        })
    }

}