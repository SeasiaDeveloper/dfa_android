package com.ngo.listeners

import com.ngo.pojo.response.GetComplaintsResponse

interface OnListItemClickListener {
    fun onItemClick(complaintsData:GetComplaintsResponse.Data,type:String)
}