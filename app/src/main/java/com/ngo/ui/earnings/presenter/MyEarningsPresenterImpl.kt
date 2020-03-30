package com.ngo.ui.earnings.presenter

import com.ngo.pojo.response.MyEarningsResponse
import com.ngo.ui.earnings.model.MyEarningsModel
import com.ngo.ui.earnings.view.MyEarningsView

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