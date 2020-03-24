package com.ngo.ui.ngoform.presenter


import com.ngo.pojo.request.NGORequest
import com.ngo.pojo.response.NGOResponse
import com.ngo.ui.ngoform.model.NGOFormModel
import com.ngo.ui.ngoform.view.NGOFormView

class NGOFormPresenterImpl(private var view: NGOFormView) : NGOFormPresenter {
    private var model: NGOFormModel = NGOFormModel(this)

    override fun onSaveDetailsSuccess(response: NGOResponse) {
        view.showNGOResponse(response)    }

    override fun onSaveDetailsFailed(error: String) {
        view.showServerError(error)    }

    override fun saveDetailsRequest(request: NGORequest) {
        model.sendNGORequest(request)    }

    override fun showError(error: String) {
        view.showServerError(error)    }

}