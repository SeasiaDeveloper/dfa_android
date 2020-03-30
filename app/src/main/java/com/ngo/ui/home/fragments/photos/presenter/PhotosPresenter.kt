package com.ngo.ui.home.fragments.photos.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetPhotosResponse

interface PhotosPresenter : BasePresenter {
    fun getPhotos(token: String?,request: GetPhotosRequest)
    fun onGetPhotosSuccess(response: GetPhotosResponse)
    fun onGetPhotosFailed(error: String)
    fun getComplaintDetails(request: CrimeDetailsRequest, token: String?)
    fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse)
    fun getCrimeDetailsFailure(error: String)
}