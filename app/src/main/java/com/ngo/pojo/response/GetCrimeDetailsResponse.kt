package com.ngo.pojo.response

data class GetCrimeDetailsResponse(
    var media_list: Array<String>,

    var latitude: String,

    var crime_type: String,

    var type: String,

    var report_data: String,

    var user_id: String,

    var report_time: String,

    var urgency: String,

    var showDelete: String,

    var id: String,

    var crime_type_id: String,

    var longitude: String,

    var info: String,

    var status: String
)