package com.dfa.ui.home.fragments.videos.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.request.GetPhotosRequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetPhotosResponse

interface VideoPresenter: BasePresenter {
    fun getVideos(token:String?,request: GetPhotosRequest)
    fun onGetVideosSuccess(response: GetPhotosResponse)
    fun onGetVideosFailed(error: String)
    fun getComplaintDetails(request: CrimeDetailsRequest, token: String?)
    fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure(error: String)
}