package com.dfa.ui.comments.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.DeleteComplaintResponse
import com.dfa.pojo.response.GetCommentsResponse

interface CommentsView : BaseView {

    fun onGetCommentsSuccess(response: GetCommentsResponse)
    fun onCommentsAddedSuccess(response: DeleteComplaintResponse)
}