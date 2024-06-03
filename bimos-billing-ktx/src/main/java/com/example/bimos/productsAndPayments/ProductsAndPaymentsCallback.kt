package com.example.bimos.productsAndPayments

import com.example.bimos.productsAndPayments.models.ProductsAndPayments
import retrofit2.Call

interface ProductsAndPaymentsCallback {
    fun onSuccessfulResponse(productsAndPayments: ProductsAndPayments)
    fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?)
    fun onFailure(call: Call<ProductsAndPayments>, t: Throwable)
}