package com.dfa.pojo.response

import java.io.Serializable

class GetCrimeDetailsResponse : Serializable {
    val code: Int? = null

    val data: Array<Data>? = null

    val message: String? = null

    inner class Data : Serializable {

        val media_list: Array<String>? = null

        val userDetail: UserDetail? = null

        val latitude: String? = null
        var transfered_to: String? = null

        val crime_type: String? = null

        val media_type:String?=null

        val type: String? = null

        val report_data: String? = null

        val user_id: String? = null

        val report_time: String? = null

        val urgency: String? = null

        val showDelete: String? = null

        val id: String? = null

        val crime_type_id: String? = null

        val longitude: String? = null

        val info: String? = null

        val status: String? = null

        val ngo_comment:String?=null

        inner class UserDetail : Serializable {
            val isVerified: String? = null

            val address_1: String? = null

            val address_2: String? = null

            val pin_code: String? = null

            val mobile: String? = null

            val profile_pic: String? = null

            val last_name: String? = null

            val middle_name: String? = null

            val district: String? = null

            val id: String? = null

            val first_name: String? = null

            val email: String? = null

            val username: String? = null
        }
    }
}