package com.ngo.ui.home.fragments.home.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.response.*

interface HomePresenter : BasePresenter {
    fun getProfileSuccess(getProfileResponse: GetProfileResponse)
    fun getProfileFailure(error:String)
    fun hitProfileApi(token:String?)
    fun postLocationSuccess(postLocation:PostLocationResponse)
    fun postLocationFailure(error:String)
    fun hitLocationApi(token :String?,lat:String,lng:String)
    fun saveAdhaarNo(token: String,adhaarNo:String)
    fun updateStatus(token: String, complaintId: String, statusId: String)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess)
    fun adhaarSavedSuccess(responseObject: SignupResponse)
}