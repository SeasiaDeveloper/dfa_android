package com.ngo.ui.policedetail.presenter

import com.ngo.base.presenter.BasePresenter

import com.ngo.pojo.request.PoliceDetailrequest
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.pojo.response.UpdateStatusSuccess

interface PoliceDetailPresenter : BasePresenter {
    fun crimeDetailsSuccess(getComplaintsResponse: GetCrimeDetailsResponse)
    fun crimeDetailsFailure()
    fun hitCrimeDetailsApi(crimeDetailsRequest: PoliceDetailrequest, token: String?)
    fun onListFetchedSuccess(responseObject: GetStatusResponse)
    fun updateStatus(token: String, complaintId: String, statusId: String, comment: String)
    fun fetchStatusList(token: String, userRole: String)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess)
}