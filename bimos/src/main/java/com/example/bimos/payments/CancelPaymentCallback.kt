package com.example.bimos.payments

import com.example.bimos.payments.models.CancelPaymentResponse
import com.example.bimos.payments.models.YookassaError
import com.example.bimos.productsAndPayments.models.ProductsAndPayments
import retrofit2.Call

interface CancelPaymentCallback {
    fun onCancellationPayment()
    fun onSuccessfulResponse(productsAndPayments: ProductsAndPayments)
    fun onYookassaErrorResponse(yookassaError: YookassaError)
    fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?)
    fun onFailure(call: Call<CancelPaymentResponse>, t: Throwable)
}