package com.dfa.ui.ngoform.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.NGOResponse

interface NGOFormView:BaseView {
    fun showNGOResponse(response: NGOResponse)

}