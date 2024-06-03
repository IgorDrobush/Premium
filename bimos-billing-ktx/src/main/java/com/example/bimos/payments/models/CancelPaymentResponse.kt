package com.example.bimos.payments.models

import com.example.bimos.productsAndPayments.models.ProductsAndPayments

data class CancelPaymentResponse(
    val productsAndPayments: ProductsAndPayments,
    val yookassaError: YookassaError?
)