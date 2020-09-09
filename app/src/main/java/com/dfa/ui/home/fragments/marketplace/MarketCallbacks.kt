package com.dfa.ui.home.fragments.marketplace

import com.dfa.pojo.response.MarketPlaceResponse

interface MarketCallbacks {
    fun onSuccess(responseObject: MarketPlaceResponse)
    fun onDeleteBusiness(data: String)
    fun onFailer(s: String)
}