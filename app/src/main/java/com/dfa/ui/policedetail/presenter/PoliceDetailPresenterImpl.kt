package com.dfa.ui.policedetail.presenter

import com.dfa.pojo.request.PoliceDetailrequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetStatusResponse
import com.dfa.pojo.response.UpdateStatusSuccess
import com.dfa.ui.policedetail.model.PoliceDetailModel
import com.dfa.ui.policedetail.view.PoliceDetailView

class PoliceDetailPresenterImpl(private var policeView: PoliceDetailView) :
    PoliceDetailPresenter {

    private var policeDetailsModel: PoliceDetailModel = PoliceDetailModel(this)

        override fun crimeDetailsSuccess(getComplaintsResponse: GetCrimeDetailsResponse) {
            policeView.getCrimeDetailsSuccess(getComplaintsResponse)
        }

        override fun crimeDetailsFailure() {
            policeView.getCrimeDetailsFailure()
        }

        override fun hitCrimeDetailsApi(crimeDetailsRequest: PoliceDetailrequest, token: String?) {
            policeDetailsModel.getCrimeComplaints(token,crimeDetailsRequest)
        }

        override fun showError(error: String) {
            policeView.showServerError(error)
        }

    override fun onListFetchedSuccess(responseObject: GetStatusResponse) {
        policeView.onListFetchedSuccess(responseObject)
    }

    //fetch the list of status i.e assigned etc based on role
    override fun fetchStatusList(token: String, userRole: String) {
        policeDetailsModel.fetchStatusList(token,userRole)
    }

    override fun updateStatus(token: String, complaintId: String, statusId: String, comment: String) {
        policeDetailsModel.updateStatus(token,complaintId,statusId,comment)
    }

    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess) {
        policeView.statusUpdationSuccess(responseObject)
    }
}