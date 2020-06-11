package com.ngo.ui.home.fragments.cases.presenter

import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.request.CreatePostRequest
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.response.*
import com.ngo.ui.home.fragments.cases.model.CasesModel
import com.ngo.ui.home.fragments.cases.view.CasesView

class CasesPresenterImplClass(private var view:CasesView) : CasesPresenter {

    var model = CasesModel(this)

    override fun createPost(request: CreatePostRequest,token: String?) {
        //hit post api
        model.createPost(request,token)
    }

    override fun getComplaints(casesRequest: CasesRequest,token: String, userRole: String) {
        model.fetchComplaints(casesRequest,token,userRole)
    }

    override fun showError(error: String) {
        view.showServerError(error)
    }

    override fun onGetCompaintsSuccess(response: GetCasesResponse) {
        view.showGetComplaintsResponse(response)
    }

    override fun onGetCompaintsFailed(error: String) {
        view.showServerError(error)
    }

    fun onPostAdded(responseObject: CreatePostResponse) {
        view.onPostAdded(responseObject)
    }

    override fun deleteComplaint(token: String , id: String) {
      model.deleteComplaint(token , id)
    }
  override fun hideComplaint(token: String , id: String) {
      model.hideComplaint(token , id)
    }

    override fun onComplaintDeleted(responseObject: DeleteComplaintResponse) {
        view.onComplaintDeleted(responseObject)
    }

    override fun changeLikeStatus(token: String, id: String) {
       model.changeLikeStatus(token , id)
    }

    override fun onLikeStatusChanged(responseObject: DeleteComplaintResponse) {
        view.onLikeStatusChanged(responseObject)
    }

    override fun saveAdhaarNo(token: String, adhaarNo: String) {
        model.saveAdhaarNo(token,adhaarNo)
    }

    override fun adhaarSavedSuccess(responseObject: SignupResponse) {
        view.adhaarSavedSuccess(responseObject)
    }

    //fetch the list of status i.e assigned etc based on role
    override fun fetchStatusList(token: String, userRole: String) {
       model.fetchStatusList(token,userRole)
    }

    override fun onListFetchedSuccess(responseObject: GetStatusResponse) {
       view.onListFetchedSuccess(responseObject)
    }

    //update the status of the complaint
    override fun updateStatus(token: String, complaintId: String, statusId: String, comment:String) {
        model.updateStatus(token,complaintId,statusId, comment)
    }

    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess) {
        view.statusUpdationSuccess(responseObject)
    }

    override fun callFirIamageApi(token: String, complaintId: CrimeDetailsRequest) {
        model.callFirImageApi(token,complaintId)
    }

    override fun getfirImageResponse(response:FirImageResponse) {
        view.getFirImageData(response)
    }
}
