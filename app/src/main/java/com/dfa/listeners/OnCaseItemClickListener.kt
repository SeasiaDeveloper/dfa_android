package com.dfa.listeners

import com.dfa.pojo.response.GetCasesResponse

interface OnCaseItemClickListener {
    fun onItemClick(complaintsData: GetCasesResponse.Data, type:String,position:Int)

    //to delete the complaint/post
    fun onDeleteItem(complaintsData: GetCasesResponse.Data)

    //to change the like status
    fun changeLikeStatus(complaintsData: GetCasesResponse.Data)

    fun onStatusClick(statusId: String)
}