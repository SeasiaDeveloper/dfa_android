package com.ngo.ui.policedetail.presenter

import com.ngo.pojo.request.PoliceDetailrequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
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
}