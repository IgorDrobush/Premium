package com.example.bimos.products.models

data class Product(
    val id: String,
    val applicationId: String,
    val type: String,
    val subscriptionPeriod: Int,
    val name: String,
    val description: String,
    val price: Int,
    val pricePerMonth: Int,
    val number: Int,
    val currency: String,
    val discount: String,
    val fullPrice: Int,
    val applyNewPrice: Boolean,
    val trialPeriod: Int,
    val promo: String,
    val tag: String
)