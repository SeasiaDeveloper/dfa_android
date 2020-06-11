package com.ngo.ui.emergency.presenter

import com.ngo.pojo.request.EmergencyDataRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.EmergencyDataResponse
import com.ngo.ui.emergency.model.EmergencyFragmentModel
import com.ngo.ui.emergency.view.EmergencyFragmentView

class EmegencyFragmentPresenterImpl(private var emergencyFragmentView: EmergencyFragmentView) :
    EmergencyFragmentPresenter {
    private var emergencyFragmentModel: EmergencyFragmentModel = EmergencyFragmentModel(this)
    override fun hitEmergencyApi(request: EmergencyDataRequest, token: String?) {
        emergencyFragmentModel.hitEmergencyApi(request, token)
    }

    override fun emergencySuccess(myEarningsResponse: EmergencyDataResponse) {
        emergencyFragmentView.getEmergencyDataSuccess(myEarningsResponse)
    }

    override fun emergencyFailure(error: String) {
        emergencyFragmentView.showServerError(error)
    }

    override fun districtsSuccess(response: DistResponse) {
        emergencyFragmentView.getDistrictsSuccess(response)
    }

    override fun hitDistricApi() {
        emergencyFragmentModel.getDist()
    }

    override fun showError(error: String) {
        emergencyFragmentView.showServerError(error)
    }
}