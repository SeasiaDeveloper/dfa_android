package com.ngo.ui.generalpublic.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.ComplaintRequest
import com.ngo.pojo.response.ComplaintResponse
import com.ngo.pojo.response.GetCrimeTypesResponse

interface PublicComplaintPresenter:BasePresenter {
    fun onEmptyLevel()
    fun onEmptyImage()
    fun onEmptyDescription()
    fun onValidationSuccess()
    fun onGetCrimeTypes(token: String?)
    fun onGetCrimeTypesSuccess(getCrimeTypesResponse: GetCrimeTypesResponse)
    fun onGetCrimeTypesFailure(error:String)
    fun checkValidations(level:Int,image:String,description:String)
    fun onSaveDetailsSuccess(response: ComplaintResponse)
    fun onSaveDetailsFailed(error: String)
    fun saveDetailsRequest(token:String?,request: ComplaintRequest)
}