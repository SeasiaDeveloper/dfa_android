package com.dfa.pojo.response

data class GetPoliceFormData(
    val `data`: List<PoliceFormData>,
    val message: String,
    val status: Int
)

data class PoliceFormData(
    val comment: String,
    val forward_id: String,
    val created_at: String,
    val crime: String,
    val description: String,
    val device_token: String,
    val email: String,
    val id: String,
    val image: String,
    val lat: String,
    val level: String,
    val lng: String,
    val name: String,
    val orderby: String,
    val phone: String,
    val status: String,
    val police_comment: String?,
    val updated_at: String,
    val user_type: String
)