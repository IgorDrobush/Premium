package com.example.bimos.productsAndPayments

import com.example.bimos.products.models.Product
import com.example.bimos.productsAndPayments.models.ProductsAndPaymentsLists
import com.example.bimos.productsAndPayments.models.ProductsAndPayments

object ProductsAndPaymentsMapper {

    fun getProductsAndPaymentsLists(productsAndPayments: ProductsAndPayments): ProductsAndPaymentsLists {

//        return if (productsAndPayments != null) {
//
//        } else {
//            ProductsAndPaymentsLists(
//                productList = listOf(),
//                productMap = mutableMapOf(),
//                paymentList = listOf()
//            )
//        }

        val products = productsAndPayments.products
        val payments = productsAndPayments.payments

        val productMap = mutableMapOf<String, Product>()
        for (product in products) {
            productMap[product.id] = product
        }

        return ProductsAndPaymentsLists(
            productList = products,
            productMap = productMap,
            paymentList = payments
        )
    }
}