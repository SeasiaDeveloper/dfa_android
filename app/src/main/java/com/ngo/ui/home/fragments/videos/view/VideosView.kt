package com.ngo.ui.home.fragments.videos.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetPhotosResponse

interface VideosView : BaseView {

    fun showGetVideosResponse(response: GetPhotosResponse)

    fun getVideosFailure(error: String)

}
