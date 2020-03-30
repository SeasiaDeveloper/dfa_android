package com.ngo.pojo.response

import java.io.Serializable

class GetProfileResponse {
    val code: Int? = null
    val data: Data? = null
    val message: String? = null

    inner class Data : Serializable {
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

        var adhar_number:String? =null
    }
}