package com.dfa.ui.home.fragments.cases.view

import android.location.Location


interface LocationListenerCallback {
    fun updateUi(location: Location)
    fun onLocationNotFound()
}