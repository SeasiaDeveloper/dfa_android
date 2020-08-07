package com.dfa.ui.generalpublic.presenter

import com.dfa.pojo.response.AddPoliceComplainResponse
import com.dfa.pojo.response.PoliceStationResponse

interface PoliceStationCallback {
    fun getPoliceStation(body:PoliceStationResponse)
    fun addPoliceComplaint(body: AddPoliceComplainResponse)
    fun error(serverError: String)
}