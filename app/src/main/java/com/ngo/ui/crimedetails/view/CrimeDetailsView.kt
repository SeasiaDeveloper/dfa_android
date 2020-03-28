package com.ngo.ui.crimedetails.view

import com.ngo.base.view.BaseView

interface CrimeDetailsView : BaseView {
    fun getCrimeDetailsSuccess()
    fun getCrimeDetailsFailure()
}