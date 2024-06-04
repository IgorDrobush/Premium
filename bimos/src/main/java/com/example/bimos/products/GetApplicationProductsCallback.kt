package com.example.bimos.products

import com.example.bimos.products.models.Product
import retrofit2.Call

interface GetApplicationProductsCallback {
    fun onSuccessfulResponse(products: List<Product>)
    fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?)
    fun onFailure(call: Call<List<Product>>, t: Throwable)
}