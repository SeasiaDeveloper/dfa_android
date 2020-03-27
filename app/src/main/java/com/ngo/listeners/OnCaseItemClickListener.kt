package com.ngo.listeners

import com.ngo.pojo.response.GetCasesResponse

interface OnCaseItemClickListener {
    fun onItemClick(complaintsData: GetCasesResponse.DataBean, type:String)
    fun onDeleteItem(complaintsData: GetCasesResponse.DataBean)
    fun changeLikeStatus(complaintsData: GetCasesResponse.DataBean)
}