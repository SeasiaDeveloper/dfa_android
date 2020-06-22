package com.dfa.ui.comments.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.response.DeleteComplaintResponse
import com.dfa.pojo.response.GetCommentsResponse

interface CommentsPresenter:BasePresenter {
    fun fetchComments(id: String, token: String)
    fun onGetCommentsSuccess(response: GetCommentsResponse)
    fun onAddComment(token: String, id: String, comment:String)
    fun onCommentsAddedSuccess(responseObject: DeleteComplaintResponse)
}