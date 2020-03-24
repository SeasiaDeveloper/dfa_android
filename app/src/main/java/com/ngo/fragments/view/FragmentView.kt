package com.ngo.fragments.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetPoliceFormResponse

interface FragmentView:BaseView {
    fun showPoliceFormResponse(response: GetPoliceFormResponse)
}