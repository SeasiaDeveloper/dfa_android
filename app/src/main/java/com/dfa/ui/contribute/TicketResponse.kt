package com.dfa.ui.contribute

import java.io.Serializable

class TicketResponse:Serializable {
    var next_page: String? = null

    var code: String? = null
    var lottery_date: String? = null
    var lottery_place: String? = null

    var data: ArrayList<Data>?=null
    var lottery_price: ArrayList<String>?=null



    var total_rows: String? = null

    var show_tickets: String? = null

    var total_page: String? = null

    var message: String? = null
    var ticket_cost: String? = null

    var current_page: String? = null

    class Data:Serializable{
        var Ticket: String? = null

        var BucketId: String? = null
        var isSelected: Boolean? = false


    }

}