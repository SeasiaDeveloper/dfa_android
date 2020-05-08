package com.ngo.ui.commentlikelist.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCommentsResponse

interface CommentLikeUserView : BaseView {

    fun onGetCommentsListSuccess(response: GetCommentsResponse)
    fun onGetLikeListSuccess(response: GetCommentsResponse)
}