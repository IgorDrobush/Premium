package com.example.bimos.payments

interface ConfirmationPaymentCallback {
    fun onCanceledConfirmationResult()
    fun onErrorConfirmationResult(code: Int?, description: String?, url: String?)
}