package com.dfa.ui.crimedetails.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetStatusResponse
import com.dfa.pojo.response.UpdateStatusSuccess

interface CrimeDetailsView : BaseView {
    fun getCrimeDetailsSuccess(getCrimeTypesResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure()
    fun onListFetchedSuccess(responseObject: GetStatusResponse)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess, complaint: UpdateStatusSuccess.Data)
}