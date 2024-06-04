package com.example.bimos.payments.models

data class Payment(
    val paymentId: String, // 50
    val userUid: String, // 100
    val productId: String, // 50
    val applicationId: String, // 50
    val status: String, // 30
    val confirmationUrl: String,
    val cancellationReason: String, // 50
    val initialPrice: String, // 20
    val currentPrice: String, // 20
    val currency: String, // 10
    val paymentTime: Long,
    val activationTime: Long,
    val renewalTime: Long,
    val endOfTermTime: Long,
    val type: String, // 30
    val subscriptionPeriod: Int,
//    val shopId: Int,
//    val key: String, // 100
    val paymentConfirmed: Boolean,
    val trialPeriod: Int,
    val test: Boolean
)