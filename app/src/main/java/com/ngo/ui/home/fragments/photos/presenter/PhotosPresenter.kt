package com.ngo.ui.home.fragments.photos.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.GetPhotosResponse

interface PhotosPresenter : BasePresenter {
    fun getPhotos(request: GetPhotosRequest)
    fun onGetPhotosSuccess(response: GetPhotosResponse)
    fun onGetPhotosFailed(error: String)
}