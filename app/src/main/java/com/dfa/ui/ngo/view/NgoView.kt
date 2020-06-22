package com.dfa.ui.ngo.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.GetComplaintsResponse

interface NgoView : BaseView {
    fun showGetComplaintsResponse(response: GetComplaintsResponse)
    fun showDescError()
}
