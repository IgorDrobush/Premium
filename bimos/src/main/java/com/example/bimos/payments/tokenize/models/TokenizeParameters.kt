package com.example.bimos.payments.tokenize.models

import android.content.Context
import com.example.bimos.products.models.Product

data class TokenizeParameters(
    val customerId: String,
    val product: Product,
    val context: Context
)
