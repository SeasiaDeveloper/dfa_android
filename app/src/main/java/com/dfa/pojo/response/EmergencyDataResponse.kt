package com.dfa.pojo.response

import java.io.Serializable

 class EmergencyDataResponse {
    val code: Int? = null
    val data: ArrayList<Data>? = null
    val message: String? = null

    inner class Data : Serializable {
        val district_name: String? = null
        val emergency_type: String? = null
        val address: String? = null
        val type_id: String? = null
        val latitude: String? = null
        val name: String? = null
        val mobile: ArrayList<String>? = null
        val created_at: String? = null
        val id: String? = null
        val district_id: String? = null
        val longitude: String? = null
    }
}