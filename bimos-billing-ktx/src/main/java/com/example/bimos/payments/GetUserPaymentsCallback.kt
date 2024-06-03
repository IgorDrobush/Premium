package com.example.bimos.payments

import com.example.bimos.payments.models.Payment
import retrofit2.Call

interface GetUserPaymentsCallback {
    fun onSuccessfulResponse(payments: List<Payment>)
    fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?)
    fun onFailure(call: Call<List<Payment>>, t: Throwable)
}