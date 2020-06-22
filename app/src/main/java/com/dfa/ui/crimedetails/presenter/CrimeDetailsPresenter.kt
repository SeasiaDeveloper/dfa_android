package com.dfa.ui.crimedetails.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetStatusResponse
import com.dfa.pojo.response.UpdateStatusSuccess

interface CrimeDetailsPresenter : BasePresenter {
    fun crimeDetailsSuccess(getComplaintsResponse: GetCrimeDetailsResponse)
    fun crimeDetailsFailure()
    fun hiCrimeDetailsApi(crimeDetailsRequest: CrimeDetailsRequest,token:String?)
    fun fetchStatusList(token: String, userRole: String)
    fun onListFetchedSuccess(responseObject: GetStatusResponse)
    fun updateStatus(token: String, complaintId: String, statusId: String,comment:String)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess, complaint: UpdateStatusSuccess.Data)
}