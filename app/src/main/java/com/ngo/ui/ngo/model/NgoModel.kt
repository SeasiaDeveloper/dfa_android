package com.ngo.ui.ngo.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.ui.ngo.presenter.NgoPresenter
import com.ngo.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class NgoModel(private var presenter: NgoPresenter){

    fun fetchCompalints() {
        val service = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val response = service.getcomplains()
        response.enqueue(object : Callback<GetComplaintsResponse> {
            override fun onResponse(call: Call<GetComplaintsResponse>, response: Response<GetComplaintsResponse>) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.status == 200) {
                        presenter.onGetCompaintsSuccess(responseObject)
                    } else {
                        presenter.onGetCompaintsFailed(response.body()?.message ?: Constants.SERVER_ERROR)
                    }
                } else {
                    presenter.showError(Constants.SERVER_ERROR)
                }
            }


            override fun onFailure(call: Call<GetComplaintsResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    presenter.showError("Socket Time error")
                }else{
                    presenter.showError(t.message + "")
                }
            }
        })


    }

}
