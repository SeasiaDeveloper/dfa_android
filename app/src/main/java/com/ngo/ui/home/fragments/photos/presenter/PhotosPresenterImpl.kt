package com.ngo.ui.home.fragments.photos.presenter

import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetPhotosResponse
import com.ngo.ui.home.fragments.photos.model.PhotosModel
import com.ngo.ui.home.fragments.photos.view.PhotosView

class PhotosPresenterImpl(private var view: PhotosView) : PhotosPresenter {

    private var model: PhotosModel = PhotosModel(this)

    override fun getPhotos(token:String?,request: GetPhotosRequest) {
        model.fetchPhotos(token,request)
    }

    override fun onGetPhotosSuccess(response: GetPhotosResponse) {
        view.showGetPhotosResponse(response)
    }

    override fun onGetPhotosFailed(error: String) {
        view.getPhotosFailure(error)
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