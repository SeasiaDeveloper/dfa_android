package com.dfa.ui.generalpublic.presenter

import com.dfa.pojo.response.AssignOfficedResponse
import com.dfa.pojo.response.PoliceOfficerResponse

interface PoilceOfficercallback {
    fun getPoliceOfficer(body: PoliceOfficerResponse)
    fun assignPoliceOfficer(body: AssignOfficedResponse)
    fun  error(serverError: String)
}