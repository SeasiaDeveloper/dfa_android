package com.dfa.pojo.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class PStationsListResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<Datum>? = null

    @SerializedName("message")
    @Expose
    var message:String? = null

    inner class Datum {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("station_type_id")
        @Expose
        var stationTypeId: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("district_id")
        @Expose
        var districtId: String? = null

        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("mobile")
        @Expose
        var mobile: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("website")
        @Expose
        var website: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("policeofficer")
        @Expose
        var policeofficer: String? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

    }
}