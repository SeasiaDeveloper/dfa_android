package com.dfa.pojo.response

import java.io.Serializable

class GetCasesResponse : Serializable {

    val data: ArrayList<Data>? = null
    val message: String? = null
    val code: Int? = null

    inner class Data : Serializable {
        var isApiHit :Boolean = false
        val is_assigned: String? = null
        var fir_image: String?= null
        var transfered_to: String?= null
        val fir_url:String?=null
        val fir_mile: String?=null
        val fir_km: String?=null
        val fir_distance:String?=null
        val id: String? = null
        val user_id: String? = null
        val showDelete: Int? = null
        val longitude: String? = null
        val latitude: String? = null
        val info: String? = null
        val report_data: String? = null
        val police_station_id:String?=null
        val stationName:String?=null
        val report_time: String? = null
        val crime_type_id: String? = null
        val urgency: String? = null
        val type: String? = null
        var status: String? = null
        val media_list: List<String>? = null
        val media_type: String? = null
        val crime_type: String? = null
        var like_count: String? = null
        var comment_count: String? = null
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
