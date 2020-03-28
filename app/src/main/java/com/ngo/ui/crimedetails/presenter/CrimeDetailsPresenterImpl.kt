package com.ngo.ui.crimedetails.presenter

import com.ngo.ui.crimedetails.model.CrimeDetailsModel
import com.ngo.ui.crimedetails.view.CrimeDetailsView

class CrimeDetailsPresenterImpl(private var crimeDetailsView: CrimeDetailsView) :
    CrimeDetailsPresenter {
    private var crimeDetailsModel: CrimeDetailsModel = CrimeDetailsModel(this)
    override fun crimeDetailsSuccess() {
        crimeDetailsView.getCrimeDetailsSuccess()
    }

    override fun crimeDetailsFailure() {
        crimeDetailsView.getCrimeDetailsFailure()
    }

    override fun hiCrimeDetailsApi(complaintId: String, token: String?) {
        crimeDetailsModel.getCrimeComplaints(token, complaintId)
    }

    override fun showError(error: String) {
        crimeDetailsView.showServerError(error)
    }
}