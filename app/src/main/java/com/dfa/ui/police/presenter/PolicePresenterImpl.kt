package com.dfa.ui.police.presenter

import com.dfa.pojo.request.PoliceStatusRequest
import com.dfa.pojo.response.GetPoliceFormData
import com.dfa.pojo.response.PoliceStatusResponse
import com.dfa.ui.police.model.PoliceModel
import com.dfa.ui.police.view.PoliceView

class PolicePresenterImpl (private var policeView: PoliceView) : PolicePresenter {
    override fun onPoliceDetailsSuccess(response: GetPoliceFormData) {
        policeView.showPoliceDetailsResponse(response)    }

    override fun onPoliceDetailsFailed(error: String) {
        policeView.showServerError(error)    }

    override fun getPoliceDetailsRequest() {
        model.getNGODetailsForPoliceRequest()    }

    override fun showError(error: String) {
        policeView.showServerError(error)      }

    private var model: PoliceModel = PoliceModel(this)
    override fun onPoliceStatusSuccess(response: PoliceStatusResponse) {
        policeView.showPoliceStatusResponse(response)    }

    override fun onPoliceStatusFailed(error: String) {
        policeView.showServerError(error)    }

    override fun savePoliceStatusRequest(request: PoliceStatusRequest) {
        model.sendNGORequest(request)    }

}