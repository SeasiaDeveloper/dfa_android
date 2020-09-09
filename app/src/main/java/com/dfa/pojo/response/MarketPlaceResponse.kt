package com.dfa.pojo.response

class MarketPlaceResponse {
    var next_page: String? = null

    var code: String? = null

    var data: Array<Data>? = null

    var total_rows: String? = null

    var total_page: String? = null

    var message: String? = null

    var current_page: String? = null

    class Data {
        var pincode: String? = null

        var product: String? = null

        var address: String? = null

        var category_name: String? = null

        var contact_person: String? = null

        var latitude: String? = null

        var mobile: String? = null

        var created_at: String? = null

        var banner_path: String? = null

        var category_id: String? = null

        var added_by: String? = null

        var name: String? = null

        var distance_in_km: String? = null

        var id: String? = null

        var longitude: String? = null

        var status: String? = null

    }
}