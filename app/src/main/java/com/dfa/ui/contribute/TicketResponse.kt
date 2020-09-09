package com.dfa.ui.contribute

class TicketResponse {
    var next_page: String? = null

    var code: String? = null

    var data: ArrayList<Data>?=null

    var total_rows: String? = null

    var show_tickets: String? = null

    var total_page: String? = null

    var message: String? = null

    var current_page: String? = null
    var isSelected: Boolean? = false

    class Data{
        var Ticket: String? = null

        var BucketId: String? = null

    }

}