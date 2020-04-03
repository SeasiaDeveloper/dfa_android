package com.ngo.ui.policedetail.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetCrimeDetailsResponse

interface PoliceDetailView : BaseView {
    fun getCrimeDetailsSuccess(getCrimeTypesResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure()
}