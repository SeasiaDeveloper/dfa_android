package com.dfa.ui.ngo.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.response.GetComplaintsResponse
import com.dfa.ui.ngo.presenter.NgoPresenter
import com.dfa.utils.Constants
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
                    presenter.showError("Somthing went wrong, please try again latter")
                }
            }
        })


    }

}
