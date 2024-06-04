package com.example.bimos.products.models

data class ProductsLists(
    var productList: List<Product>,
    var productMap: MutableMap<String, Product>
)
