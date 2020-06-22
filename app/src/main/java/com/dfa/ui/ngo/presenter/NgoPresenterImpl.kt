package com.dfa.ui.ngo.presenter

import com.dfa.pojo.response.GetComplaintsResponse
import com.dfa.ui.ngo.model.NgoModel
import com.dfa.ui.ngo.view.NgoView


class NgoPresenterImpl(private var view: NgoView) : NgoPresenter {
    private var model: NgoModel = NgoModel(this)

    override fun getComplaints() {
      model.fetchCompalints()
    }

    override fun showError(error: String) {
        view.showServerError(error)
    }


    override fun onGetCompaintsSuccess(response: GetComplaintsResponse) {
      view.showGetComplaintsResponse(response)
    }

    override fun onGetCompaintsFailed(error: String) {
        view.showServerError(error)
    }
}

