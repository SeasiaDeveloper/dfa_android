package com.ngo.ui.home.fragments.videos.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetPhotosResponse

interface VideoPresenter: BasePresenter {
    fun getVideos(request: GetPhotosRequest)
    fun onGetVideosSuccess(response: GetPhotosResponse)
    fun onGetVideosFailed(error: String)
    fun getComplaintDetails(request: CrimeDetailsRequest, token: String?)
    fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure(error: String)
}