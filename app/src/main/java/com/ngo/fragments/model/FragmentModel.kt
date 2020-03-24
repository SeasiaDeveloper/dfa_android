package com.ngo.fragments.model


import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.fragments.presenter.FragmentPresenter
import com.ngo.pojo.response.GetPoliceFormResponse
import com.ngo.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentModel(private var statusPresenter:FragmentPresenter) {

    fun getPoliceFormFromServer(id:Int) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getPoliceForm(id).enqueue(object : Callback<GetPoliceFormResponse> {
            override fun onResponse(call: Call<GetPoliceFormResponse>, response: Response<GetPoliceFormResponse>) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.status == 200) {
                        statusPresenter.onPoliceFormSuccess(responseObject)
                    } else {
                        statusPresenter.showError(responseObject.message )
                    }
                } else {
                    statusPresenter.showError(Constants.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<GetPoliceFormResponse>, t: Throwable) {
                statusPresenter.showError(t.message+"")
            }
        })
    }

}