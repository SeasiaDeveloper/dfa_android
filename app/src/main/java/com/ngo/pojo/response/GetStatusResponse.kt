package com.ngo.pojo.response

data class GetStatusResponse(
    val message: String,
    val code: Int,
    var data: List<GetStatusDataBean>
)

data class GetStatusDataBean(
    var id: String,
    var name: String,
    var isChecked: Boolean
)