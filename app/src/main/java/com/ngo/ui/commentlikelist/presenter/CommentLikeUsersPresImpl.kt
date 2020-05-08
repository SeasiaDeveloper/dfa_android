package com.ngo.ui.commentlikelist.presenter

import com.ngo.pojo.response.GetCommentsResponse
import com.ngo.ui.commentlikelist.model.CommentLikeUsersModel
import com.ngo.ui.commentlikelist.view.CommentLikeUserView

class CommentLikeUsersPresImpl(private var view: CommentLikeUserView):CommentLikeUsersPresenter  {

    var model = CommentLikeUsersModel(this)

    override fun getLikeList(id: String, token: String) {
        model.getLikeListSuccess(id,token)
    }

    override fun getCommentList(id: String, token: String) {
        model.getCommentListSuccess(id,token)
    }

    override fun getLikeListSuccess(response: GetCommentsResponse) {
        view.onGetCommentsListSuccess(response)
    }

    override fun getCommentListSuccess(response: GetCommentsResponse) {
        view.onGetLikeListSuccess(response)
    }

    override fun showError(error: String) {
        view.showServerError(error)
    }
}