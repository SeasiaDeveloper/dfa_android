package com.dfa.ui.emergency.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.EmergencyDataRequest
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.EmergencyDataResponse

interface EmergencyFragmentPresenter: BasePresenter {
    fun hitEmergencyApi(request: EmergencyDataRequest,token:String?)
    fun emergencySuccess(myEarningsResponse: EmergencyDataResponse)
    fun emergencyFailure(error:String)
    fun districtsSuccess(response: DistResponse)
    fun hitDistricApi()
}