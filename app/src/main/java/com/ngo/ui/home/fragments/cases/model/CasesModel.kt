package com.ngo.ui.home.fragments.cases.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.request.CreatePostRequest
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CasesModel(private var presenter: CasesPresenterImplClass) {

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    private val imgMediaType="image/*"

    fun fetchComplaints(casesRequest: CasesRequest) {
        val retrofitApi = ApiClient.getClientWithToken().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["all"] = toRequestBody(casesRequest.all) //zero for my case 1 for all case
        map["search"] = toRequestBody(casesRequest.search)
        map["type"] = toRequestBody(casesRequest.type) //type = 0 for cases
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

    fun createPost(request: CreatePostRequest) {
        val retrofitApi = ApiClient.getClientWithToken().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["info"] = toRequestBody(request.info)
        map["media_type"] = toRequestBody(request.media_type)


        val file = File(request.post_pics[0])
        val post_pics = MultipartBody.Part.createFormData("post_pics", file.name,
            RequestBody.create(MediaType.parse(imgMediaType), file)
        )

        retrofitApi.addPost(map, post_pics).enqueue(object : Callback<GetCasesResponse> {
            override fun onFailure(call: Call<GetCasesResponse>, t: Throwable) {
                presenter.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<GetCasesResponse>,
                response: Response<GetCasesResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        presenter.onPostAdded(responseObject)
                    } else {
                        presenter.showError(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    presenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }

}