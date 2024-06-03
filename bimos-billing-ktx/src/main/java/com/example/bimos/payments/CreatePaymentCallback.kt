package com.example.bimos.payments

import com.example.bimos.payments.models.Payment
import com.example.bimos.payments.models.PaymentResponse
import com.example.bimos.payments.models.YookassaError
import retrofit2.Call

interface CreatePaymentCallback {
    fun onCreatingPayment()
    fun onSuccessfulResponse(payment: Payment)
    fun onYookassaErrorResponse(yookassaError: YookassaError)
    fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?)
    fun onFailure(call: Call<PaymentResponse>, t: Throwable)
}