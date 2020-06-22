package com.dfa.listeners

import com.dfa.pojo.response.GetComplaintsResponse

interface OnListItemClickListener {
    fun onItemClick(complaintsData:GetComplaintsResponse.Data,type:String)
}