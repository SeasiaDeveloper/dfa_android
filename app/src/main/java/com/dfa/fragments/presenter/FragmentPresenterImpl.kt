package com.dfa.fragments.presenter

import com.dfa.fragments.model.FragmentModel
import com.dfa.fragments.view.FragmentView
import com.dfa.pojo.response.GetPoliceFormResponse


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