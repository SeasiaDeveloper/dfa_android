package com.dfa.ui.commentlikelist.presenter

import com.dfa.pojo.response.GetCommentsResponse
import com.dfa.ui.commentlikelist.model.CommentLikeUsersModel
import com.dfa.ui.commentlikelist.view.CommentLikeUserView

class CommentLikeUsersPresImpl(private var view: CommentLikeUserView):CommentLikeUsersPresenter  {

    var model = CommentLikeUsersModel(this)

    override fun getLikeList(id: String, token: String) {
        model.getLikeListSuccess(id,token)
    }

    override fun getCommentList(id: String, token: String) {
        model.getCommentListSuccess(id,token)
    }

    override fun getLikeListSuccess(response: GetCommentsResponse) {
        view.onGetLikeListSuccess(response)
    }

    override fun getCommentListSuccess(response: GetCommentsResponse) {
        view.onGetCommentsListSuccess(response)
    }

    override fun showError(error: String) {
        view.showServerError(error)
    }
}