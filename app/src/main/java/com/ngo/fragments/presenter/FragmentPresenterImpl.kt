package com.ngo.fragments.presenter

import com.ngo.fragments.model.FragmentModel
import com.ngo.fragments.view.FragmentView
import com.ngo.pojo.response.GetPoliceFormResponse


class FragmentPresenterImpl(private var policeView: FragmentView) : FragmentPresenter {

    override fun onPoliceFormSuccess(response: GetPoliceFormResponse) {
        policeView.showPoliceFormResponse(response)    }

    override fun onPoliceFormFailed(error: String) {
        policeView.showServerError(error)    }

    override fun savePoliceFormRequest(id: Int) {
        model.getPoliceFormFromServer(id)
    }

    override fun showError(error: String) {
        policeView.showServerError(error)   }

    private var model: FragmentModel = FragmentModel(this)

}