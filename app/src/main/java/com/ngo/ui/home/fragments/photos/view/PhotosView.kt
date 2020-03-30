package com.ngo.ui.home.fragments.photos.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetPhotosResponse

interface PhotosView : BaseView {
    fun showGetPhotosResponse(response: GetPhotosResponse)
    fun getPhotosFailure(error: String)
    fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure(error: String)
}
