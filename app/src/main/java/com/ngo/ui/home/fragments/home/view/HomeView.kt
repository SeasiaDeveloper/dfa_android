package com.ngo.ui.home.fragments.home.view


import com.ngo.pojo.response.GetProfileResponse
import com.ngo.pojo.response.PostLocationResponse

interface HomeView {
    fun onGetProfileSucess(getProfileResponse: GetProfileResponse)
    fun ongetProfileFailure(error: String)
    fun onShowError(error: String)
    fun onPostLocationSucess(postLocationResponse: PostLocationResponse)
    fun onPostLocationFailure(error: String)
}