package com.dfa.fragments.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.GetPoliceFormResponse

interface FragmentView:BaseView {
    fun showPoliceFormResponse(response: GetPoliceFormResponse)
}