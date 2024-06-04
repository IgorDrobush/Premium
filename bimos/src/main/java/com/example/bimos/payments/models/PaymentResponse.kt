package com.example.bimos.payments.models

data class PaymentResponse(
    val payment: Payment?,
    val yookassaError: YookassaError?
)