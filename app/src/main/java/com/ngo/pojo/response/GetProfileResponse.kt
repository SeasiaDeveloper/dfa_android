package com.ngo.pojo.response

import java.io.Serializable

class GetProfileResponse {
    val code: Int? = null
    val data: Data? = null
    val message: String? = null

    inner class Data : Serializable {
        val isVerified: String? = null

        val user_role: String? = null

        val address_1: String? = null

        val address_2: String? = null

        val pin_code: String? = null

        val mobile: String? = null

        var profile_pic: String? = null

        val last_name: String? = null

        val middle_name: String? = null

        val district: String? = null

        val id: String? = null

        val first_name: String? = null

        val email: String? = null

        val username: String? = null

        var adhar_number:String? =null

        var app_url:String ?= null

        var ngo_phone:String ?= null

        val ngo_name: String? = null

        val ngo_address: String? = null

        val ngo_email: String? = null

        val ngo_dist: String? = null

        val ngo_state: String? = null

        val ngo_pincode: String? = null

        var ngo_latitude:String? =null

        var ngo_longitude:String ?= null

        var ur_user_user_status:String ?= null

    }

}