package com.dfa.pojo.request

data class ChangePasswordRequest(
    val password: String,
    val confirm_password: String,
    val user_id: String
)