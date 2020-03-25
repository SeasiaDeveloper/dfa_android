package com.ngo.pojo.response

data class ComplaintResponse(
    val message: String,
    val code: Int,
    val data: Array<Data>
)

data class Data(
    val date_time: String,
    val urgency: String,
    val media_list: Array<String>,
    val latitude: String,
    val crime_type: String,
    val id: String,
    val longitude: String,
    val info: String,
    val status: String
)