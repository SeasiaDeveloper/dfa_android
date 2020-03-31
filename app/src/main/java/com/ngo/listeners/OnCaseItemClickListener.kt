package com.ngo.listeners

import com.ngo.pojo.response.GetCasesResponse

interface OnCaseItemClickListener {
    fun onItemClick(complaintsData: GetCasesResponse.Data, type:String)

    //to delete the complaint/post
    fun onDeleteItem(complaintsData: GetCasesResponse.Data)

    //to change the like status
    fun changeLikeStatus(complaintsData: GetCasesResponse.Data)

    fun onStatusClick(statusId: String)
}