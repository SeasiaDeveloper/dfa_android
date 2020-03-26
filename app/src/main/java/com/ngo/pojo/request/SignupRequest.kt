package com.ngo.pojo.request

class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val middle_name: String,
    val district_id: String,
    val address_1: String,
    val address_2: String,
    val pin_code: String,
    val mobile: String,
    val adhar_number: String,
    val profile_pic:String,
    val confirmPass:String,
    val device_token :String
)