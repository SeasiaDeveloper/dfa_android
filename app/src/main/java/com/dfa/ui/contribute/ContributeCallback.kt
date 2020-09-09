package com.dfa.ui.contribute

interface ContributeCallback {
    fun onSuccess(responseObject: TicketResponse)
    fun onFailed(s: String)
}