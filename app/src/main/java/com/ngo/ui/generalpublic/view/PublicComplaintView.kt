package com.ngo.ui.generalpublic.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.ComplaintResponse

interface PublicComplaintView : BaseView {
    fun showComplaintsResponse(complaintsResponse: ComplaintResponse)
    fun showEmptyImageError()
    fun onValidationSuccess()
    fun showEmptyLevelError()
    fun showEmptyDescError()

}