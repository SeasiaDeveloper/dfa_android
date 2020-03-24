package com.ngo.ui.ngo.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetComplaintsResponse

interface NgoView : BaseView {
    fun showGetComplaintsResponse(response: GetComplaintsResponse)
    fun showDescError()
}
