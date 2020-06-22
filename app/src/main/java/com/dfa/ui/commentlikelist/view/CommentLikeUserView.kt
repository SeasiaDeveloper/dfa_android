package com.dfa.ui.commentlikelist.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.GetCommentsResponse

interface CommentLikeUserView : BaseView {

    fun onGetCommentsListSuccess(response: GetCommentsResponse)
    fun onGetLikeListSuccess(response: GetCommentsResponse)
}