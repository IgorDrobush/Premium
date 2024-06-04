package com.example.bimos.productsAndPayments.models

import com.example.bimos.payments.models.Payment
import com.example.bimos.products.models.Product

data class ProductsAndPaymentsLists(
    var productList: List<Product>,
    var productMap: MutableMap<String, Product>,
    var paymentList: List<Payment>
)
