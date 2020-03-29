package com.ngo.ui.comments.presenter

import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCommentsResponse
import com.ngo.ui.comments.model.CommentsModel
import com.ngo.ui.comments.view.CommentsView

class CommentsPresenterImplClass(private var view: CommentsView) : CommentsPresenter {

    var model = CommentsModel(this)

    override fun fetchComments(id: String, token: String) {
        model.fetchComments(id, token)
    }

    override fun onGetCommentsSuccess(response: GetCommentsResponse) {
       view.onGetCommentsSuccess(response)
    }

    override fun onAddComment(token: String, id: String, comment:String) {
        model.onAddComment(id, token,comment)
    }

    override fun onCommentsAddedSuccess(responseObject: DeleteComplaintResponse) {
        view.onCommentsAddedSuccess(responseObject)
    }

    override fun showError(error: String) {
        view.showServerError(error)
    }

}
