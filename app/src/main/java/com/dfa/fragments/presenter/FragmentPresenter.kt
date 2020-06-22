package com.dfa.fragments.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.response.GetPoliceFormResponse

interface FragmentPresenter:BasePresenter {
    fun onPoliceFormSuccess(response:GetPoliceFormResponse )
    fun onPoliceFormFailed(error: String)
    fun savePoliceFormRequest(id:Int)
}