package com.dfa.ui.generalpublic.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.ComplaintResponse
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.GetCrimeTypesResponse
import com.dfa.pojo.response.PStationsListResponse

interface PublicComplaintView : BaseView {
    fun showComplaintsResponse(complaintsResponse: ComplaintResponse)
    fun showEmptyImageError()
    fun onValidationSuccess()
    fun showEmptyLevelError()
    fun showEmptyDescError()
    fun getCrimeTypesListSuccess(getCrimeTypesResponse: GetCrimeTypesResponse)
    fun getCrimeTyepLstFailure(error: String)
    fun getDistrictsSuccess(response: DistResponse)
    fun getpStationSuccess(response: PStationsListResponse)

}