package com.dfa.ui.home.fragments.cases.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.CasesRequest
import com.dfa.pojo.request.CreatePostRequest
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.response.*

interface CasesPresenter:BasePresenter {
    fun getComplaints(casesRequest: CasesRequest,token: String, userRole: String)
    fun onGetCompaintsSuccess(response: GetCasesResponse)
    fun onGetCompaintsFailed(error: String)
    fun createPost(request: CreatePostRequest,token: String?)
    fun deleteComplaint(token: String, id: String)
    fun hideComplaint(token: String, id: String)
    fun onComplaintDeleted(responseObject: DeleteComplaintResponse)
    fun changeLikeStatus(token: String, id: String)
    fun onLikeStatusChanged(responseObject: DeleteComplaintResponse)
    fun saveAdhaarNo(token: String,adhaarNo:String)
    fun adhaarSavedSuccess(responseObject: SignupResponse)
    fun fetchStatusList(token: String, userRole: String)
    fun onListFetchedSuccess(responseObject: GetStatusResponse)
    fun updateStatus(token: String, complaintId: String, statusId: String,comment:String)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess)
    fun callFirIamageApi(token: String, complaintId: CrimeDetailsRequest)
    fun getfirImageResponse(response:FirImageResponse)
    fun advertisementSuccess(responseObject: AdvertisementResponse)
    fun advertisementInput(responseObject: AdvertisementInput)
}