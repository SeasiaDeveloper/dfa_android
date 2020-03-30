package com.ngo.ui.home.fragments.cases.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.pojo.response.SignupResponse

interface CasesView : BaseView {
    fun showGetComplaintsResponse(response: GetCasesResponse)
    fun showDescError()
    fun onPostAdded(responseObject: GetCasesResponse)
    fun onComplaintDeleted(responseObject: DeleteComplaintResponse)
    fun onLikeStatusChanged(responseObject: DeleteComplaintResponse)
    fun adhaarSavedSuccess(responseObject: SignupResponse)
}