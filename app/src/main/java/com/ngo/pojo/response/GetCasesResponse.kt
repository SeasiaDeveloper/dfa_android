package com.ngo.pojo.response

import java.io.Serializable

class GetCasesResponse : Serializable {

    val data: ArrayList<Data>? = null
    val message: String? = null
    val code: Int? = null

    inner class Data : Serializable {
        val is_assigned: String? = null
        val fir_url:String?=null
        val id: String? = null
        val user_id: String? = null
        val showDelete: Int? = null
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
        val like_count: String? = null
        val comment_count: String? = null
        var is_liked: Int? = null
        val userDetail: UserDetail? = null

        inner class UserDetail : Serializable {
            val last_name: String? = null
            val id: String? = null
            val first_name: String? = null
            val email: String? = null
            val username: String? = null
            val profile_pic: String? = null
        }
    }
}
