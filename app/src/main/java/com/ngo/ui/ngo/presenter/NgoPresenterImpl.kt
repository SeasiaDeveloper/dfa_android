package com.ngo.ui.ngo.presenter

import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.ui.ngo.model.NgoModel
import com.ngo.ui.ngo.view.NgoView


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

