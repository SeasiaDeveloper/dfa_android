package com.dfa.ui.earnings.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.response.MyEarningsResponse

interface MyEarningsPresenter:BasePresenter{
    fun hitMyEarningsApi(contactNumber:String?,token:String?)
    fun myEarningsSuccess(myEarningsResponse: MyEarningsResponse)
    fun myEarningsFailure(error:String)
}