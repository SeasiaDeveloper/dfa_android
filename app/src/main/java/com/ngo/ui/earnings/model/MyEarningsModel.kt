package com.ngo.ui.earnings.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.pojo.response.MyEarningsResponse
import com.ngo.ui.earnings.presenter.MyEarningsPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyEarningsModel(private var myEarningsPresenter: MyEarningsPresenter) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun myEarningsApi(contactNumber: String?, token: String?) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getMyEarningsData(token,contactNumber?.toLong()!!).enqueue(object :
            Callback<MyEarningsResponse> {
            override fun onFailure(call: Call<MyEarningsResponse>, t: Throwable) {
                myEarningsPresenter.showError(Constants.SERVER_ERROR)
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