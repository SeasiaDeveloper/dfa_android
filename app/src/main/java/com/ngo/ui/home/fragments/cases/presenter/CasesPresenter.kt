package com.ngo.ui.home.fragments.cases.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.response.GetCasesResponse

interface CasesPresenter:BasePresenter {
    fun getComplaints(casesRequest: CasesRequest)
    fun onGetCompaintsSuccess(response: GetCasesResponse)
    fun onGetCompaintsFailed(error: String)
}