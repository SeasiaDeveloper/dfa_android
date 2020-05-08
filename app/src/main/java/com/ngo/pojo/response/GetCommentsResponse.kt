package com.ngo.pojo.response

import java.io.Serializable

class GetCommentsResponse : Serializable {
    val message: String? = null
    val code: Int? = null
    val data: ArrayList<CommentData>? = null

    inner class CommentData : Serializable {
        val id: String? = null
        val ID: String?=null
        val user_id: String? = null
        val comment: String? = null
        val first_name: String? = null
        val last_name: String? = null
        val profile_pic: String? = null
        val display_name: String? = null
    }
}
