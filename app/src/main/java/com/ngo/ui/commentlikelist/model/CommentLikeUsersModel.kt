package com.ngo.ui.commentlikelist.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.response.GetCommentsResponse
import com.ngo.ui.commentlikelist.presenter.CommentLikeUsersPresImpl
import com.ngo.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentLikeUsersModel(var presenter: CommentLikeUsersPresImpl) {

    fun getLikeListSuccess(id: String, token: String) {
        //hit api to getLikes list to the corresponding id
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
                        presenter.getLikeListSuccess(responseObject)
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
                presenter.showError(t.message + "")
            }
        })
    }


    fun getCommentListSuccess(id: String, token: String) {
        //hit api to getComments list to the corresponding id
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
                        presenter.getCommentListSuccess(responseObject)
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
                presenter.showError(t.message + "")
            }
        })
    }
}