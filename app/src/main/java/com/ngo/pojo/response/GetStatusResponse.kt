package com.ngo.pojo.response

data class GetStatusResponse(
    val message: String,
    val code: Int,
    val data: List<GetStatusDataBean>

)

data class GetStatusDataBean(
    val id: String,
    val name: String,
    var isChecked: Boolean
)