package com.dfa.ui.earnings.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.response.MyEarningsResponse
import com.dfa.ui.earnings.presenter.MyEarningsPresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class MyEarningsModel(private var myEarningsPresenter: MyEarningsPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun myEarningsApi(contactNumber: String?, token: String?) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getMyEarningsData(token,contactNumber?.toLong()!!).enqueue(object :
            Callback<MyEarningsResponse> {
            override fun onFailure(call: Call<MyEarningsResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    myEarningsPresenter.showError("Socket Time error")
                }else{
                    myEarningsPresenter.showError(t.message + "")
                }
            }

            override fun onResponse(
                call: Call<MyEarningsResponse>,
                response: Response<MyEarningsResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        myEarningsPresenter.myEarningsSuccess(responseObject)
                    } else {
                        myEarningsPresenter.myEarningsFailure(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    myEarningsPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}