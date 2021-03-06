package com.dfa.pojo.response

class SignupResponse(
    val message: String,
    val code: Int,
    val data: Data,
    val token:String
)

data class Data(
    val status:String,
    val id: String,
    val email: String,
    val username: String,
    val first_name: String,
    val last_name: String,
    val middle_name: String,
    val district: String,
    val address_1: String,
    val address_2: String,
    val pin_code: String,
    val mobile: String,
    val adhar_number:String,
    val profile_pic: String
)