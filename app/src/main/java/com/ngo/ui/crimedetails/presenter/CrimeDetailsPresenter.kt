package com.ngo.ui.crimedetails.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.pojo.response.UpdateStatusSuccess

interface CrimeDetailsPresenter : BasePresenter {
    fun crimeDetailsSuccess(getComplaintsResponse: GetCrimeDetailsResponse)
    fun crimeDetailsFailure()
    fun hiCrimeDetailsApi(crimeDetailsRequest: CrimeDetailsRequest,token:String?)
    fun fetchStatusList(token: String, userRole: String)
    fun onListFetchedSuccess(responseObject: GetStatusResponse)
    fun updateStatus(token: String, complaintId: String, statusId: String,comment:String)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess, complaint: UpdateStatusSuccess.Data)
}