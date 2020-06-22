package com.dfa.ui.earnings.presenter

import com.dfa.pojo.response.MyEarningsResponse
import com.dfa.ui.earnings.model.MyEarningsModel
import com.dfa.ui.earnings.view.MyEarningsView

class MyEarningsPresenterImpl(private var myEarningsView: MyEarningsView) :
    MyEarningsPresenter {
    private var myEarningsModel: MyEarningsModel = MyEarningsModel(this)
    override fun hitMyEarningsApi(contactNumber: String?, token: String?) {
        myEarningsModel.myEarningsApi(contactNumber, token)
    }

    override fun myEarningsSuccess(myEarningsResponse: MyEarningsResponse) {
        myEarningsView.myEarningssuccess(myEarningsResponse)
    }

    override fun myEarningsFailure(error: String) {
        myEarningsView.myEarningFailure(error)
    }

    override fun showError(error: String) {
        myEarningsView.showServerError(error)
    }
}