package com.dfa.ui.ngoform.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.NGORequest
import com.dfa.pojo.response.NGOResponse

interface NGOFormPresenter:BasePresenter {
    fun onSaveDetailsSuccess(response: NGOResponse)
    fun onSaveDetailsFailed(error: String)
    fun saveDetailsRequest(request: NGORequest)
}