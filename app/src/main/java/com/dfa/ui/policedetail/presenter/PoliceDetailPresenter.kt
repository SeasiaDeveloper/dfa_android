package com.dfa.ui.policedetail.presenter

import com.dfa.base.presenter.BasePresenter

import com.dfa.pojo.request.PoliceDetailrequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetStatusResponse
import com.dfa.pojo.response.UpdateStatusSuccess

interface PoliceDetailPresenter : BasePresenter {
    fun crimeDetailsSuccess(getComplaintsResponse: GetCrimeDetailsResponse)
    fun crimeDetailsFailure()
    fun hitCrimeDetailsApi(crimeDetailsRequest: PoliceDetailrequest, token: String?)
    fun onListFetchedSuccess(responseObject: GetStatusResponse)
    fun updateStatus(token: String, complaintId: String, statusId: String, comment: String)
    fun fetchStatusList(token: String, userRole: String)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess)
}