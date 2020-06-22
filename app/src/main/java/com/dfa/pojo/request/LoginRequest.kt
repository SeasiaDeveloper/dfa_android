package com.dfa.pojo.request

data class LoginRequest (
    val username: String,
    val password: String,
    val device_token:String
)