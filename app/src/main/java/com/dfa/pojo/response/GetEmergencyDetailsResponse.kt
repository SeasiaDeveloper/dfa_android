package com.dfa.pojo.response

class GetEmergencyDetailsResponse {
    val message: String? = null
    val code: Int? = null
    val data: ArrayList<Details>? = null

    class Details {
        var id: String? = null
        var name: String? = null
        var contact_number: String? = null
    }
}
