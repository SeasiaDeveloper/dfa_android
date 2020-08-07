package com.dfa.pojo.response

class PoliceOfficerResponse {
   var code: String? = null

   var data: ArrayList<Data>?=null

   var message: String? = null
   class Data{
      var latitude: String? = null

      var rank: String? = null

      var police_officer_id: String? = null

      var id: String? = null

      var designation: String? = null

      var police_station_id: String? = null

      var officer_name: String? = null

      var longitude: String? = null

      var status: String? = null

      var isSelected: String? = "false"
   }

}