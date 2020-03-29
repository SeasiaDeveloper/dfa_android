package com.ngo.ui.home.fragments.home.presenter

import com.ngo.pojo.response.GetProfileResponse
import com.ngo.ui.home.fragments.home.model.HomeModel
import com.ngo.ui.home.fragments.home.view.HomeView

class HomePresenterImpl(private var homeView: HomeView) :
    HomePresenter {
    private var homeModel: HomeModel = HomeModel(this)
    override fun getProfileSuccess(getProfileResponse: GetProfileResponse) {
        homeView.onGetProfileSucess(getProfileResponse)
    }

    override fun getProfileFailure(error: String) {
        homeView.ongetProfileFailure(error)
    }


    override fun hitProfileApi(token: String?) {
        homeModel.getProfileData(token)
    }

    override fun showError(error: String) {
//
    }
}