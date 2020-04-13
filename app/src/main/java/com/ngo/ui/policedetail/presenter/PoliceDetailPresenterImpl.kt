package com.ngo.ui.policedetail.presenter

import com.ngo.pojo.request.PoliceDetailrequest
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.pojo.response.UpdateStatusSuccess
import com.ngo.ui.policedetail.model.PoliceDetailModel
import com.ngo.ui.policedetail.view.PoliceDetailView

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