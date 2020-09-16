package com.dfa.pojo.response

class AdvertisementResponse {
    var next_page: String? = null

    var data: ArrayList<Data>? = null

    var total_rows: String? = null

    var total_page: String? = null
    var due: String? = null
    var ticket_cost: String? = null


    var current_page: String? = null

    class Data {

        var path: String? = null

        var visibility: String? = null

        var id: String? = null

        var external_link: String? = null
    }
}