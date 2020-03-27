package com.ngo.pojo.response

import java.io.Serializable

class GetPhotosResponse : Serializable {
    val code: Int? = null

    val data: List<Data>? = null

    val message: String? = null

    inner class Data {
         val complaint_id: String? = null

         val id: String? = null

         val url: String? = null

         val status: String? = null
    }
}