package com.ngo.ui.home.fragments.cases.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetCasesResponse

interface CasesView :BaseView {
    fun showGetComplaintsResponse(response: GetCasesResponse)
    fun showDescError()
    fun onPostAdded(responseObject: GetCasesResponse)
}