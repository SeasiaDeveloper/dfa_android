package com.dfa.ui.contribute.payment

interface PaymentCallback {
    fun updateStatusSuccess(message: String?)
    fun failer(s: String)
}