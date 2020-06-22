package com.dfa.ui.home.fragments.photos.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.request.GetPhotosRequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetPhotosResponse

interface PhotosPresenter : BasePresenter {
    fun getPhotos(token: String?,request: GetPhotosRequest)
    fun onGetPhotosSuccess(response: GetPhotosResponse)
    fun onGetPhotosFailed(error: String)
    fun getComplaintDetails(request: CrimeDetailsRequest, token: String?)
    fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure(error: String)
}