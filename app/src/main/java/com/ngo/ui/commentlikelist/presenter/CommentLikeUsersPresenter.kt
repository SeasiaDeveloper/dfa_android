package com.ngo.ui.commentlikelist.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.response.GetCommentsResponse

interface CommentLikeUsersPresenter: BasePresenter {
    fun getLikeList(id: String, token:String)
    fun getCommentList(id: String,token:String)
    fun getLikeListSuccess(response: GetCommentsResponse)
    fun getCommentListSuccess(response: GetCommentsResponse)
}