package com.ngo.ui.home.fragments.cases.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.request.CreatePostRequest
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCasesResponse

interface CasesPresenter:BasePresenter {
    fun getComplaints(casesRequest: CasesRequest,token: String)
    fun onGetCompaintsSuccess(response: GetCasesResponse)
    fun onGetCompaintsFailed(error: String)
    fun createPost(request: CreatePostRequest,token: String?)
    fun deleteComplaint(token: String, id: String)
    fun onComplaintDeleted(responseObject: DeleteComplaintResponse)
}