package com.ngo.listeners

import com.ngo.pojo.response.GetCasesResponse

interface OnCaseItemClickListener {
    fun onItemClick(complaintsData: GetCasesResponse.DataBean, type:String)

    //to delete the complaint/post
    fun onDeleteItem(complaintsData: GetCasesResponse.DataBean)

    //to change the like status
    fun changeLikeStatus(complaintsData: GetCasesResponse.DataBean)
}