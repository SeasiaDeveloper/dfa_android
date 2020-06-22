package com.dfa.pojo.response

import java.io.Serializable

class UpdateStatusSuccess : Serializable {
    val code: Int? = null

    val data: Array<Data>? = null

    val message: String? = null

    inner class Data : Serializable {
        var fir_url: String? = null

        var ngo_comment: String? = null

        var media_list: Array<String>? = null

        var userDetail: UserDetail? = null

        var is_assigned: String? = null

        var latitude: String? = null

        var crime_type: String? = null

        var type: String? = null

        var report_data: String? = null

        var date_time: String? = null

        var user_id: String? = null

        var report_time: String? = null

        var urgency: String? = null

        var media_type: String? = null

        var showDelete: String? = null

        var id: String? = null

        var crime_type_id: String? = null

        var longitude: String? = null

        var info: String? = null

        var status: String? = null

        inner class UserDetail : Serializable {
            val isVerified: String? = null

            val address_1: String? = null

            val address_2: String? = null

            val pin_code: String? = null

            val mobile: String? = null

            val profile_pic: String? = null

            val last_name: String? = null

            val middle_name: String? = null

            val user_role: String? = null

            val district: String? = null

            val id: String? = null

            val first_name: String? = null

            val email: String? = null

            val username: String? = null

        }
    }
}