package com.ngo.ui.crimedetails.presenter


import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.pojo.response.UpdateStatusSuccess
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

    //fetch the list of status i.e assigned etc based on role
    override fun fetchStatusList(token: String, userRole: String) {
        crimeDetailsModel.fetchStatusList(token,userRole)
    }

    override fun onListFetchedSuccess(responseObject: GetStatusResponse) {
        crimeDetailsView.onListFetchedSuccess(responseObject)
    }

    //update the status of the complaint
    override fun updateStatus(token: String, complaintId: String, statusId: String, comment:String) {
        crimeDetailsModel.updateStatus(token,complaintId,statusId, comment)
    }

    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess, complaint: UpdateStatusSuccess.Data) {
        crimeDetailsView.statusUpdationSuccess(responseObject,complaint)
    }
}