package com.dfa.ui.generalpublic.model

import android.widget.Toast
import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.AssignedOfficerInput
import com.dfa.pojo.response.AssignOfficedResponse
import com.dfa.pojo.response.PoliceOfficerResponse
import com.dfa.ui.generalpublic.PoliceOfficerActivity
import com.dfa.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class PoliceOfficerPresenter(private var context: PoliceOfficerActivity) {

    fun getTermsConditions(token: String?) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getPoliceOfficer(token).enqueue(object :
            Callback<PoliceOfficerResponse> {
            override fun onResponse(
                call: Call<PoliceOfficerResponse>,
                response: Response<PoliceOfficerResponse>
            ) {
                if (response.isSuccessful) {
                    context.getPoliceOfficer(response.body()!!)
                } else if(response!=null) {
                    context.error(response.message())
                } else{
                    context.error(Constants.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<PoliceOfficerResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    context.error("Socket Time error")
                }else{
                    context.error(t.message + "")
                }
            }
        })
    }



    fun addOfficerConditions(
        input: AssignedOfficerInput?,
        token: String
    ) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.assignedOfficer(token,input!!).enqueue(object :
            Callback<AssignOfficedResponse> {
            override fun onResponse(
                call: Call<AssignOfficedResponse>,
                response: Response<AssignOfficedResponse>
            ) {
                if (response.isSuccessful) {
                    context.assignPoliceOfficer(response.body()!!)
                } else {
                    context.error(Constants.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<AssignOfficedResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    context.error("Socket Time error")
                }else{
                    context.error(t.message + "")
                }
            }
        })
    }


}