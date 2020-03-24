package com.ngo.fragments.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.PoliceStatusRequest
import com.ngo.pojo.response.GetPoliceFormResponse
import com.ngo.pojo.response.PoliceStatusResponse

interface FragmentPresenter:BasePresenter {
    fun onPoliceFormSuccess(response:GetPoliceFormResponse )
    fun onPoliceFormFailed(error: String)
    fun savePoliceFormRequest(id:Int)
}