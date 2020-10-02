package com.dfa.ui.contribute.payment

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.application.MyApplication
import com.dfa.pojo.request.PaymentInput
import com.dfa.pojo.response.PaymentResponse
import com.dfa.utils.PreferenceHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class PaymentPresenter(var paymentActivity: PaymentActivity) {



    fun paymentStatus(input: PaymentInput) {
        val token =
            PreferenceHandler.readString(
                MyApplication.instance,
                PreferenceHandler.AUTHORIZATION,
                ""
            )
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.paymentStatus( input).enqueue(object :
            Callback<PaymentResponse> {

            override fun onResponse(
                call: Call<PaymentResponse>,
                response: Response<PaymentResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code==200) {
                        paymentActivity.updateStatusSuccess(responseObject.message)
                    } else {
                        paymentActivity.failer("Somthing went wrong")
                    }
                } else {
                    paymentActivity.failer("Server Error")
                }
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    paymentActivity.failer("Socket Time error")
                } else {
                    paymentActivity.failer("Somthing went wrong, please try again latter")
                }
            }
        })
    }

}