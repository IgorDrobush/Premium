package com.example.bimos.payments.models

data class YookassaError(
    val type: String,
    val id: String,
    val code: String,
    val description: String,
    val parameter: String
)