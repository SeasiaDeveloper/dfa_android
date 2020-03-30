package com.ngo.ui.earnings.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.MyEarningsResponse

interface MyEarningsView:BaseView {
    fun myEarningssuccess(myEarningsResponse: MyEarningsResponse)
    fun myEarningFailure(error:String)
}