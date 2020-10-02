package com.dfa.ui.contribute

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.application.MyApplication
import com.dfa.utils.PreferenceHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class ContributePresenter(
   var context: ContributeActivity) {

    fun getMarketPlaceData(input: TickerInput) {
        val token =
            PreferenceHandler.readString(
                MyApplication.instance,
                PreferenceHandler.AUTHORIZATION,
                ""
            )
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getTicketList( input).enqueue(object :
            Callback<TicketResponse> {

            override fun onResponse(
                call: Call<TicketResponse>,
                response: Response<TicketResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.data != null) {
                        context.onSuccess(responseObject)
                    } else {
                        context.onFailed("Somthing went wrong")
                    }
                } else {
                    context.onFailed("Server Error")
                }
            }

            override fun onFailure(call: Call<TicketResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    context.onFailed("Socket Time error")
                } else {
                    context.onFailed("Somthing went wrong, please try again latter")
                }
            }
        })
    }
}