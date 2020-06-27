package com.dfa.pojo.response

import java.io.Serializable

class NotificationResponse : Serializable {
    var username:String?=null
    var complaint_id:String?=null
    var report_data:String ?=null
     var report_time:String ?=null
     var description:String ?=null
    var is_notify:String?=null
    var urgency: String? = null
    var longitude: String? = null
    var latitude: String? = null
    var status: String? = null
    var crime_type: String? = null


}