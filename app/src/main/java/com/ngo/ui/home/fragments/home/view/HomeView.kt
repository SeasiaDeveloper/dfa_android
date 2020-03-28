package com.ngo.ui.home.fragments.home.view


import com.ngo.pojo.response.GetProfileResponse

interface HomeView {
    fun onGetProfileSucess(getProfileResponse: GetProfileResponse)
    fun ongetProfileFailure(error: String)
    fun onShowError(error: String)
}