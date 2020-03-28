package com.ngo.ui.crimedetails.presenter


import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.ui.crimedetails.model.CrimeDetailsModel
import com.ngo.ui.crimedetails.view.CrimeDetailsView

class CrimeDetailsPresenterImpl(private var crimeDetailsView: CrimeDetailsView) :
    CrimeDetailsPresenter {
    private var crimeDetailsModel: CrimeDetailsModel = CrimeDetailsModel(this)

    override fun crimeDetailsSuccess(getComplaintsResponse: GetCrimeDetailsResponse) {
        crimeDetailsView.getCrimeDetailsSuccess(getComplaintsResponse)
    }

    override fun crimeDetailsFailure() {
        crimeDetailsView.getCrimeDetailsFailure()
    }

    override fun hiCrimeDetailsApi(crimeDetailsRequest: CrimeDetailsRequest, token: String?) {
        crimeDetailsModel.getCrimeComplaints(token,crimeDetailsRequest)
    }

    override fun showError(error: String) {
        crimeDetailsView.showServerError(error)
    }
}