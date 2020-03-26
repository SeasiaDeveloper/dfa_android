package com.ngo.ui.home.fragments.cases.presenter

import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.ui.home.fragments.cases.model.CasesModel
import com.ngo.ui.home.fragments.cases.view.CasesView

class CasesPresenterImplClass(private var view:CasesView) : CasesPresenter {

    var model = CasesModel(this)

    override fun getComplaints(casesRequest: CasesRequest) {
        model.fetchComplaints(casesRequest)
    }

    override fun showError(error: String) {
        view.showServerError(error)
    }


    override fun onGetCompaintsSuccess(response: GetCasesResponse) {
        view.showGetComplaintsResponse(response)
    }

    override fun onGetCompaintsFailed(error: String) {
        view.showServerError(error)
    }

}
