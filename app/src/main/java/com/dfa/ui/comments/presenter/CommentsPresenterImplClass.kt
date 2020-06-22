package com.dfa.ui.comments.presenter

import com.dfa.pojo.response.DeleteComplaintResponse
import com.dfa.pojo.response.GetCommentsResponse
import com.dfa.ui.comments.model.CommentsModel
import com.dfa.ui.comments.view.CommentsView

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
