package com.ngo.ui.emergency.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.EmergencyDataResponse

interface EmergencyFragmentView:BaseView{
    fun getEmergencyDataSuccess(myEarningsResponse: EmergencyDataResponse)
    fun getEmergencyDataFailure(error:String)
}