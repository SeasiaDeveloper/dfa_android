package com.dfa.ui.home.fragments.marketplace

import com.dfa.pojo.response.CategoriesListResponse

interface AddBusinessCallback {
    fun onSuccess(message: String?)
    fun getCategories(responseObject: CategoriesListResponse)
    fun onError(s: String)
}