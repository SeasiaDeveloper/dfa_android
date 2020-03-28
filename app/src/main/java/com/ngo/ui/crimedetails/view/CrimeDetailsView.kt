package com.ngo.ui.crimedetails.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetCrimeDetailsResponse

interface CrimeDetailsView : BaseView {
    fun getCrimeDetailsSuccess(getCrimeTypesResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure()
}