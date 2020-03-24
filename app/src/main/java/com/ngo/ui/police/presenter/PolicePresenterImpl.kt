package com.ngo.ui.police.presenter

import com.ngo.pojo.request.PoliceStatusRequest
import com.ngo.pojo.response.GetPoliceFormData
import com.ngo.pojo.response.PoliceStatusResponse
import com.ngo.ui.police.model.PoliceModel
import com.ngo.ui.police.view.PoliceView

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