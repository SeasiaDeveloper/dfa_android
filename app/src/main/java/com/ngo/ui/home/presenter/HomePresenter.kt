package com.ngo.ui.home.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.response.GetProfileResponse

interface HomePresenter : BasePresenter {
    fun getProfileSuccess(getProfileResponse: GetProfileResponse)
    fun getProfileFailure(error:String)
    fun hitProfileApi(token:String?)
}