package com.dfa.ui.commentlikelist.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.response.GetCommentsResponse

interface CommentLikeUsersPresenter: BasePresenter {
    fun getLikeList(id: String, token:String)
    fun getCommentList(id: String,token:String)
    fun getLikeListSuccess(response: GetCommentsResponse)
    fun getCommentListSuccess(response: GetCommentsResponse)
}