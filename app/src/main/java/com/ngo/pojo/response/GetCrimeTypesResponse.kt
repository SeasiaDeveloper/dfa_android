package com.ngo.pojo.response

import java.io.Serializable

class GetCrimeTypesResponse : Serializable {
    val code: Int? = null
    var data: Array<Data>? = null

    inner class Data : Serializable {
        var name: String? = null
        var id: String? = null
    }
}

