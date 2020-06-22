package com.dfa.ui.emergency.presenter

import com.dfa.pojo.request.EmergencyDataRequest
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.EmergencyDataResponse
import com.dfa.ui.emergency.model.EmergencyFragmentModel
import com.dfa.ui.emergency.view.EmergencyFragmentView

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