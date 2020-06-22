package com.dfa.ui.termsConditions.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.response.GetTermsConditionsResponse
import com.dfa.ui.termsConditions.presenter.TermsConditionsPresenter
import com.dfa.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class TermsConditionsModel(private var presenter: TermsConditionsPresenter) {

    fun getTermsConditions(token: String?) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.get_terms_condition(token).enqueue(object :
            Callback<GetTermsConditionsResponse> {
            override fun onResponse(
                call: Call<GetTermsConditionsResponse>,
                response: Response<GetTermsConditionsResponse>
            ) {
                if (response.isSuccessful) {
                    presenter.onTermsConditionsSuccess(response.body()!!)
                } else {
                    presenter.onTermsConditionsFailed(Constants.SERVER_ERROR)
                }


            }

            override fun onFailure(call: Call<GetTermsConditionsResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    presenter.showError("Socket Time error")
                }else{
                    presenter.showError(t.message + "")
                }
            }
        })
    }

}