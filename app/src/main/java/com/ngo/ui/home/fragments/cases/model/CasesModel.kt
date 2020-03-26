package com.ngo.ui.home.fragments.cases.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CasesModel(private var presenter: CasesPresenterImplClass) {

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun fetchComplaints(casesRequest: CasesRequest) {
        val retrofitApi = ApiClient.getClientWithToken().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["all"] = toRequestBody(casesRequest.all)
        map["search"] = toRequestBody(casesRequest.search)

        retrofitApi.getCases(map).enqueue(object : Callback<GetCasesResponse> {
            override fun onResponse(
                call: Call<GetCasesResponse>,
                response: Response<GetCasesResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        presenter.onGetCompaintsSuccess(responseObject)
                    } else {
                        presenter.onGetCompaintsFailed(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    presenter.showError(Constants.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<GetCasesResponse>, t: Throwable) {
                presenter.showError(t.message + "")
            }
        })
    }
}