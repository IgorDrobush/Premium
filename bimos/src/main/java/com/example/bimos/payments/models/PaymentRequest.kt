package com.example.bimos.payments.models

data class PaymentRequest(
    val userUid: String,
    val productId: String,
    val applicationId: String,
    val paymentToken: String?,
    val paymentMethodType: String,
    val returnUrl: String?,
    val value: String,
    val currency: String,
    val shopId: Int,
    val secretKey: String,
    val idempotenceKey: String,
    val description: String,
    val productType: String,
    val trialPeriod: Int,
    val subscriptionPeriod: Int?
)