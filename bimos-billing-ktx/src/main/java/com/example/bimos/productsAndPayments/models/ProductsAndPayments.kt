package com.example.bimos.productsAndPayments.models

import com.example.bimos.payments.models.Payment
import com.example.bimos.products.models.Product

data class ProductsAndPayments(
    val products: List<Product>,
    val payments: List<Payment>
)