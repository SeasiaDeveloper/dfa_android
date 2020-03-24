package com.ngo.ui.generalpublic.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.ComplaintRequest
import com.ngo.pojo.response.ComplaintResponse

interface PublicComplaintPresenter:BasePresenter {
    fun onEmptyLevel()
    fun onEmptyImage()
    fun onEmptyDescription()
    fun onValidationSuccess()
    fun checkValidations(level:Int,image:String,description:String)
    fun onSaveDetailsSuccess(response: ComplaintResponse)
    fun onSaveDetailsFailed(error: String)
    fun saveDetailsRequest(request: ComplaintRequest)
}