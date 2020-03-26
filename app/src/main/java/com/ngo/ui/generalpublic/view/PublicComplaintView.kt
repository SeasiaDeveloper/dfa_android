package com.ngo.ui.generalpublic.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.ComplaintResponse
import com.ngo.pojo.response.GetCrimeTypesResponse

interface PublicComplaintView : BaseView {
    fun showComplaintsResponse(complaintsResponse: ComplaintResponse)
    fun showEmptyImageError()
    fun onValidationSuccess()
    fun showEmptyLevelError()
    fun showEmptyDescError()
    fun getCrimeTypesListSuccess(getCrimeTypesResponse: GetCrimeTypesResponse)
    fun getCrimeTyepLstFailure(error: String)
}