package com.ngo.pojo.request

data class ComplaintRequest(
   /* val name: String,
    val phone: String,
    val email: String,*/
    val crime: String,
    val level: Int,
    val image: Array<String>,
    val description: String,
    //val device_token: String,
    val lat: String,
    val lng: String,
    val mediaType: String
)