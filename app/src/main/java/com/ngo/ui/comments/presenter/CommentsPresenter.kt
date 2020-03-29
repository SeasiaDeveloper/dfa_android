package com.ngo.ui.comments.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCommentsResponse

interface CommentsPresenter:BasePresenter {
    fun fetchComments(id: String, token: String)
    fun onGetCommentsSuccess(response: GetCommentsResponse)
    fun onAddComment(token: String, id: String, comment:String)
    fun onCommentsAddedSuccess(responseObject: DeleteComplaintResponse)
}