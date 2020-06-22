package com.dfa.ui.emergency.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.EmergencyDataResponse

interface EmergencyFragmentView:BaseView{
    fun getEmergencyDataSuccess(myEarningsResponse: EmergencyDataResponse)
    fun getEmergencyDataFailure(error:String)
    fun getDistrictsSuccess(response: DistResponse)
}