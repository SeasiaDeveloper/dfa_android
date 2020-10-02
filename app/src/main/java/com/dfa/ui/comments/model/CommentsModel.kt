package com.dfa.ui.comments.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.response.DeleteComplaintResponse
import com.dfa.pojo.response.GetCommentsResponse
import com.dfa.ui.comments.presenter.CommentsPresenterImplClass
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class CommentsModel(private var presenter: CommentsPresenterImplClass) {

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun onAddComment(id: String, token: String, comment: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(id)
        map["comment"] = toRequestBody(comment)

        retrofitApi.addComment(token, map).enqueue(object : Callback<DeleteComplaintResponse> {
            override fun onFailure(call: Call<DeleteComplaintResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    presenter.showError("Socket Time error")
                }else{
                    presenter.showError("Somthing went wrong, please try again latter")
                }
            }

            override fun onResponse(
                call: Call<DeleteComplaintResponse>,
                response: Response<DeleteComplaintResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        presenter.onCommentsAddedSuccess(responseObject)
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


    fun fetchComments(id: String, token: String) {
        //hit api to getComments to the corresponding id
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val complaintId = Integer.parseInt(id)
        retrofitApi.getComments(token, complaintId).enqueue(object : Callback<GetCommentsResponse> {
            override fun onResponse(
                call: Call<GetCommentsResponse>,
                response: Response<GetCommentsResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        presenter.onGetCommentsSuccess(responseObject)
                    } else {
                        presenter.showError(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    presenter.showError(Constants.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<GetCommentsResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    presenter.showError("Socket Time error")
                }else{
                    presenter.showError("Somthing went wrong, please try again latter")
                }
            }
        })
    }


}