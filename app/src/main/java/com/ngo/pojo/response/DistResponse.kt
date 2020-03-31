package com.ngo.pojo.response

data class DistResponse(
    val code: Int,
    val data: ArrayList<DataBean>,
    val message: String
)

data class DataBean(
    val id: String,
    val name: String
)