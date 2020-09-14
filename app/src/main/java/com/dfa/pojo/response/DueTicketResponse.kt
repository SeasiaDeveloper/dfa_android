package com.dfa.pojo.response

class DueTicketResponse {
    var due: String? = null
    var due_tickets: ArrayList<Due_tickets>? = null

    class Due_tickets {
      var Ticket: String? = null

        var  BucketId: String? = null
    }

}