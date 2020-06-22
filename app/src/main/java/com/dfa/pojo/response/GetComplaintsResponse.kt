package com.dfa.pojo.response

import java.io.Serializable

class GetComplaintsResponse : Serializable {
    val data: List<Data>? = null
    val message: String? = null
    val status: Int? = null

    inner class Data : Serializable {
        val image: String? = null
        val lng: String? = null
        val level: String? = null
        val description: String? = null
        val created_at: String? = null
        val user_type: String? = null
        val updated_at: String? = null
        val phone: String? = null
        val forwarded: Int = 0
        val device_token: String? = null
        val name: String? = null
        val crime: String? = null
        val police_comment: String = ""
        val id: String? = null
        val email: String? = null
        val lat: String? = null
        val status: String? = null
    }
}
