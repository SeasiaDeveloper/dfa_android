package com.ngo.ui.ngo.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.response.GetComplaintsResponse

interface NgoPresenter : BasePresenter {
    fun getComplaints()
    fun onGetCompaintsSuccess(response: GetComplaintsResponse)
    fun onGetCompaintsFailed(error: String)

}
