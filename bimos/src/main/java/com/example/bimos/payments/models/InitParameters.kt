package com.example.bimos.payments.models

data class InitParameters(
    val userUid: String,
    val applicationId: String,
    val shopId: Int,
    val secretKey: String,
    val clientApplicationKey: String,
    val colorScheme: String,
    val applicationName: String
)
