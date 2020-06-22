package com.dfa.ui.home.fragments.home.view

import com.dfa.pojo.response.GetProfileResponse
import com.dfa.pojo.response.PostLocationResponse
import com.dfa.pojo.response.SignupResponse
import com.dfa.pojo.response.UpdateStatusSuccess

interface HomeView {
    fun onGetProfileSucess(getProfileResponse: GetProfileResponse)
    fun ongetProfileFailure(error: String)
    fun onShowError(error: String)
    fun onPostLocationSucess(postLocationResponse: PostLocationResponse)
    fun onPostLocationFailure(error: String)
    fun statusUpdationSuccess(responseObject: UpdateStatusSuccess)
    fun adhaarSavedSuccess(responseObject: SignupResponse)
}