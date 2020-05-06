package com.ngo.ui.crimedetails.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.pojo.response.UpdateStatusSuccess

interface CrimeDetailsView : BaseView {
    fun getCrimeDetailsSuccess(getCrimeTypesResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure()
    fun onListFetchedSuccess(responseObject: GetStatusResponse)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess, complaint: UpdateStatusSuccess.Data)
}