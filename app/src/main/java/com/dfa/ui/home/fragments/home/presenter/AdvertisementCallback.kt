package com.dfa.ui.home.fragments.home.presenter

import com.dfa.pojo.response.AdvertisementResponse

interface AdvertisementCallback {
    fun advertisementSuccess(responseObject: AdvertisementResponse)
    fun failer(serverError: String)
}