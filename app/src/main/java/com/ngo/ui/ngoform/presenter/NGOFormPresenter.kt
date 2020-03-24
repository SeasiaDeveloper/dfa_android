package com.ngo.ui.ngoform.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.NGORequest
import com.ngo.pojo.response.NGOResponse

interface NGOFormPresenter:BasePresenter {
    fun onSaveDetailsSuccess(response: NGOResponse)
    fun onSaveDetailsFailed(error: String)
    fun saveDetailsRequest(request: NGORequest)
}