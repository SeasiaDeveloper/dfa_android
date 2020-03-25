package com.ngo.pojo.response

data class DistResponse(
    val code: Int,
    val data: ArrayList<DataBean>
)

data class DataBean(
    val id: String,
    val name: String
)