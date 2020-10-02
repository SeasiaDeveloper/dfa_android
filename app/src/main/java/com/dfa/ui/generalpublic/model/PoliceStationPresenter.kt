package com.dfa.ui.generalpublic.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.AddPoliceStationInput
import com.dfa.pojo.response.AddPoliceComplainResponse
import com.dfa.pojo.response.PoliceStationResponse
import com.dfa.ui.generalpublic.PoliceStationActivity
import com.dfa.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class PoliceStationPresenter(private var context: PoliceStationActivity) {

    fun getTermsConditions(token: String?) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getPoliceStation(token).enqueue(object :
            Callback<PoliceStationResponse> {
            override fun onResponse(
                call: Call<PoliceStationResponse>,
                response: Response<PoliceStationResponse>
            ) {
                if (response.isSuccessful) {
                    context.getPoliceStation(response.body()!!)
                }
                else if(response!=null) {
                    context.error(response.message())
                }
                else {
                    context.error(Constants.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<PoliceStationResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    context.error("Socket Time error")
                }else{
                    context.error("Somthing went wrong, please try again latter")
                }
            }
        })
    }


    fun addComplaint(input: AddPoliceStationInput?, token: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.addPoliceStation(token,input!!).enqueue(object :
            Callback<AddPoliceComplainResponse> {
            override fun onResponse(
                call: Call<AddPoliceComplainResponse>,
                response: Response<AddPoliceComplainResponse>
            ) {
                if (response.isSuccessful) {
                    context.addPoliceComplaint(response.body()!!)
                } else {
                    context.error(Constants.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<AddPoliceComplainResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    context.error("Socket Time error")
                }else{
                    context.error("Somthing went wrong, please try again latter")
                }
            }
        })
    }
}