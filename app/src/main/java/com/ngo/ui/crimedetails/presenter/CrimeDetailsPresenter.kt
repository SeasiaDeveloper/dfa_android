package com.ngo.ui.crimedetails.presenter

import com.ngo.base.presenter.BasePresenter

interface CrimeDetailsPresenter : BasePresenter {
    fun crimeDetailsSuccess()
    fun crimeDetailsFailure()
    fun hiCrimeDetailsApi(complaintId:String,token:String?)
}