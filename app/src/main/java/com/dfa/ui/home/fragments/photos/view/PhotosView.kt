package com.dfa.ui.home.fragments.photos.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetPhotosResponse

interface PhotosView : BaseView {
    fun showGetPhotosResponse(response: GetPhotosResponse)
    fun getPhotosFailure(error: String)
    fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure(error: String)
}
