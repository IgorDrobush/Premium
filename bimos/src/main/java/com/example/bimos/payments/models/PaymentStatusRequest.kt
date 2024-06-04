package com.example.bimos.payments.models

data class PaymentStatusRequest(
    val paymentId: String,
    val shopId: Int,
    val secretKey: String
)