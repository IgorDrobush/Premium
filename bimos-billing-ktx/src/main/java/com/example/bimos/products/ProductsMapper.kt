package com.example.bimos.products

import com.example.bimos.products.models.Product
import com.example.bimos.products.models.ProductsLists

object ProductsMapper {

    fun getProductsLists(products: List<Product>): ProductsLists {

//        return if (products != null) {
//
//
//        } else {
//            ProductsLists(
//                productList = listOf(),
//                productMap = mutableMapOf()
//            )
//        }

        val productMap = mutableMapOf<String, Product>()
        for (product in products) {
            productMap[product.id] = product
        }

        return ProductsLists(
            productList = products,
            productMap = productMap
        )
    }
}