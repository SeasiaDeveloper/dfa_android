package com.ngo.ui.emergency.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.EmergencyDataRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.EmergencyDataResponse

interface EmergencyFragmentPresenter: BasePresenter {
    fun hitEmergencyApi(request: EmergencyDataRequest,token:String?)
    fun emergencySuccess(myEarningsResponse: EmergencyDataResponse)
    fun emergencyFailure(error:String)
    fun districtsSuccess(response: DistResponse)
    fun hitDistricApi()
}