package com.dfa.ui.home.fragments.videos.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetPhotosResponse

interface VideosView : BaseView {
    fun showGetVideosResponse(response: GetPhotosResponse)
    fun getVideosFailure(error: String)
    fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure(error: String)
}
