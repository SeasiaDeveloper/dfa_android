package com.ngo.pojo.response

import java.io.Serializable

class GetCasesResponse : Serializable {

    val data: List<DataBean>? = null
    val message: String? = null
    val code: Int? = null

    inner class DataBean : Serializable {
        val id: String? = null
        val user_id: String? = null
        val showDelete: Int? = null
        val userDetail: Data? = null
        val longitude: String? = null
        val latitude: String? = null
        val info: String? = null
        val report_data: String? = null
        val report_time: String? = null
        val crime_type_id: String? = null
        val urgency: String? = null
        val type: String? = null
        val status: String? = null
        val media_list: List<String>? = null
        val crime_type: String? = null
        val like_count :String?=null
        val comment_count :String?=null
        var is_liked :Int?=null


    }


}
