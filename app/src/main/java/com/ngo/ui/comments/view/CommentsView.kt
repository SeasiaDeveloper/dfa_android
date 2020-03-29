package com.ngo.ui.comments.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCommentsResponse

interface CommentsView : BaseView {

    fun onGetCommentsSuccess(response: GetCommentsResponse)
    fun onCommentsAddedSuccess(response: DeleteComplaintResponse)
}