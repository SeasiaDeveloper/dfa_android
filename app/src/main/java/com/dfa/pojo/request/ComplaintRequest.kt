package com.dfa.pojo.request

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
    var lng: String,
    val mediaType: String,
    val address:String,
    var police_id: String,
    var culprit_number: String,
    var follow_me: String,
    var suspect_number: String

)