package com.dfa.ui.home.fragments.home.presenter

import com.dfa.pojo.response.*
import com.dfa.ui.home.fragments.home.model.HomeModel
import com.dfa.ui.home.fragments.home.view.HomeView

class HomePresenterImpl(private var homeView: HomeView) :
    HomePresenter {

    override fun hitLocationApi(token: String?, latitude: String, longitude: String) {
        homeModel.getPostLocationData(token, latitude, longitude)
    }

    override fun saveAdhaarNo(token: String, adhaarNo: String) {
        homeModel.saveAdhaarNo(token,adhaarNo)
    }

    override fun logout(token:String) {
        homeModel.logout(token)
    }

    override fun postLocationSuccess(postLocation: PostLocationResponse) {
        homeView.onPostLocationSucess(postLocation)
    }

    override fun postLocationFailure(error: String) {
        homeView.onPostLocationFailure(error)
    }

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
        homeView.onShowError(error)
    }

    //update the status of the complaint
    override fun updateStatus(token: String, complaintId: String, statusId: String) {
        homeModel.updateStatus(token, complaintId, statusId)
    }

    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess) {
        homeView.statusUpdationSuccess(responseObject)
    }

    override fun dueAmountSuccess(responseObject: DueTicketResponse) {
        homeView.dueAmountSuccess(responseObject)
    }

    override fun dueAmountInput(responseObject: String) {
        homeModel.getDueAmount(responseObject)
    }

    override fun adhaarSavedSuccess(responseObject: SignupResponse) {
        homeView.adhaarSavedSuccess(responseObject)
    }

    override fun onLogoutSuccess(responseObject: CommonResponse) {
        homeView.onLogoutSuccess(responseObject)

    }
}