package com.dfa.pojo.response

class PoliceStationResponse {

    var code: String? = null

    var data: ArrayList<Data>?=null

    var message: String? = null
    class Data{
       var latitude: String? = null

        var name: String? = null

        var distance_in_km: String? = null

        var id: String? = null

        var longitude: String? = null
        var isSelected: String? = "false"
    }
}