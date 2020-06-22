package com.dfa.ui.ngo.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.response.GetComplaintsResponse

interface NgoPresenter : BasePresenter {
    fun getComplaints()
    fun onGetCompaintsSuccess(response: GetComplaintsResponse)
    fun onGetCompaintsFailed(error: String)

}
