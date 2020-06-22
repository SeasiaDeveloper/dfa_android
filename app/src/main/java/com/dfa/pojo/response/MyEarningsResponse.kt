package com.dfa.pojo.response

import java.io.Serializable

class MyEarningsResponse : Serializable {
    val code: Int? = null
    val data: Data? = null
    val message: String? = null
    inner class Data : Serializable {
        val mobile: String? = null

        val earning: String? = null
    }
}