package com.ngo.ui.termsConditions.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.response.GetTermsConditionsResponse
import com.ngo.ui.termsConditions.presenter.TermsConditionsPresenter
import com.ngo.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                presenter.showError(t.message + "")
            }
        })
    }

}