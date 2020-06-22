package com.dfa.pojo.request

data class UpdatePasswordRequest (
    val old_password: String,
    val password: String,
    val confirm_password: String,
    val user_id: String
)