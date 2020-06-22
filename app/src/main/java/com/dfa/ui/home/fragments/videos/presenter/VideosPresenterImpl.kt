package com.dfa.ui.home.fragments.videos.presenter

import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.request.GetPhotosRequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetPhotosResponse

import com.dfa.ui.home.fragments.videos.model.VideosModel
import com.dfa.ui.home.fragments.videos.view.VideosView

class VideosPresenterImpl (private var view: VideosView): VideoPresenter {

    private var model: VideosModel = VideosModel(this)

    override fun getVideos(token: String?, request: GetPhotosRequest) {
        model.fetchVideos(token,request)
    }

    override fun onGetVideosSuccess(response: GetPhotosResponse) {
        view.showGetVideosResponse(response)
    }

    override fun onGetVideosFailed(error: String) {
        view.getVideosFailure(error)
    }

    override fun getComplaintDetails(request: CrimeDetailsRequest, token: String?) {
        model.getCrimeComplaints(token, request)
    }

    override fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse) {
        view.getCrimeDetailsSuccess(crimeDetailsResponse)
    }

    override fun getCrimeDetailsFailure(error: String) {
        view.getCrimeDetailsFailure(error)
    }

    override fun showError(error: String) {
        view.showServerError(error)
    }

}
