package com.ngo.ui.ngoform.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.NGOResponse

interface NGOFormView:BaseView {
    fun showNGOResponse(response: NGOResponse)

}