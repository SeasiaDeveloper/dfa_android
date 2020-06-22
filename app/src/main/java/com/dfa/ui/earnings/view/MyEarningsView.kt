package com.dfa.ui.earnings.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.MyEarningsResponse

interface MyEarningsView:BaseView {
    fun myEarningssuccess(myEarningsResponse: MyEarningsResponse)
    fun myEarningFailure(error:String)
}