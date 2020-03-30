package com.ngo.ui.home.fragments.videos.presenter

import android.widget.VideoView
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetPhotosResponse

import com.ngo.ui.home.fragments.videos.model.VideosModel
import com.ngo.ui.home.fragments.videos.view.VideosView

class VideosPresenterImpl (private var view: VideosView): VideoPresenter {

    private var model: VideosModel = VideosModel(this)

    override fun getVideos(request: GetPhotosRequest) {
        model.fetchVideos(request)

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
