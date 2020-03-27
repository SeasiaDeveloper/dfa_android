package com.ngo.listeners

import com.ngo.pojo.response.GetCasesResponse

interface OnCaseItemClickListener {
    fun onItemClick(complaintsData: GetCasesResponse.Data, type:String)
}