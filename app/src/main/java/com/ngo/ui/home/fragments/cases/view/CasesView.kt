package com.ngo.ui.home.fragments.cases.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.*

interface CasesView : BaseView {
    fun showGetComplaintsResponse(response: GetCasesResponse)
    fun showDescError()
    fun onPostAdded(responseObject: CreatePostResponse)
    fun onComplaintDeleted(responseObject: DeleteComplaintResponse)
    fun onLikeStatusChanged(responseObject: DeleteComplaintResponse)
    fun adhaarSavedSuccess(responseObject: SignupResponse)
    fun onListFetchedSuccess(responseObject: GetStatusResponse)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess)
}