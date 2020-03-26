package com.ngo.pojo.response

import java.io.Serializable

class ComplaintResponse : Serializable {
    val message: String? = null
    val code: Int? = null

    inner class Data : Serializable {
        val date_time: String? = null
        val urgency: String? = null
        val media_list: Array<String>? = null
        val latitude: String? = null
        val crime_type: String? = null
        val id: String? = null
        val longitude: String? = null
        val info: String? = null
        val status: String? = null
    }
}