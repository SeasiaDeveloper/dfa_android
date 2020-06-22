package com.dfa.ui.generalpublic.presenter

import android.content.Context
import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.ComplaintRequest
import com.dfa.pojo.response.ComplaintResponse
import com.dfa.pojo.response.GetCrimeTypesResponse

interface PublicComplaintPresenter:BasePresenter {
    fun onEmptyLevel()
    fun onEmptyImage()
    fun onEmptyDescription()
    fun onValidationSuccess()
    fun onGetCrimeTypes(token: String?)
    fun onGetCrimeTypesSuccess(getCrimeTypesResponse: GetCrimeTypesResponse)
    fun onGetCrimeTypesFailure(error:String)
    fun checkValidations(level:Int,image:ArrayList<String>,description:String)
    fun onSaveDetailsSuccess(response: ComplaintResponse)
    fun onSaveDetailsFailed(error: String)
    fun saveDetailsRequest(token:String?,request: ComplaintRequest,context: Context)
}