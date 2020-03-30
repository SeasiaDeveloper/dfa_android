package com.ngo.ui.earnings.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.response.MyEarningsResponse

interface MyEarningsPresenter:BasePresenter{
    fun hitMyEarningsApi(contactNumber:String?,token:String?)
    fun myEarningsSuccess(myEarningsResponse: MyEarningsResponse)
    fun myEarningsFailure(error:String)
}