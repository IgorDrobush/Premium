package com.example.bimos.apiService

import com.example.bimos.payments.models.*
import com.example.bimos.products.models.ApplicationProductsRequest
import com.example.bimos.products.models.Product
import com.example.bimos.productsAndPayments.models.ProductsAndPayments
import com.example.bimos.payments.models.CancelPaymentResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface BimosApiService {

    @Headers("Content-Type: application/json")
    @POST("payment/create")
    fun createPayment(
        @Header("Authorization") authorization: String,
        @Body paymentRequest: PaymentRequest
    ): Call<PaymentResponse>

    @Headers("Content-Type: application/json")
    @POST("payment/status")
    fun checkPaymentStatus(
        @Header("Authorization") authorization: String,
        @Body paymentStatusRequest: PaymentStatusRequest
    ): Call<PaymentResponse>

    @Headers("Content-Type: application/json")
    @POST("products")
    fun getApplicationProducts(
        @Header("Authorization") authorization: String,
        @Body applicationProductsRequest: ApplicationProductsRequest
    ): Call<List<Product>>

    @Headers("Content-Type: application/json")
    @POST("products/payments")
    fun getAllProductsAndPayments(
        @Header("Authorization") authorization: String,
        @Body paymentsListRequest: PaymentsListRequest
    ): Call<ProductsAndPayments>

    @Headers("Content-Type: application/json")
    @POST("payment/cancel")
    fun cancelPayment(
        @Header("Authorization") authorization: String,
        @Body payment: Payment
    ): Call<CancelPaymentResponse>

    @Headers("Content-Type: application/json")
    @POST("subscription/cancel")
    fun cancelSubscription(
        @Header("Authorization") authorization: String,
        @Body payment: Payment
    ): Call<ProductsAndPayments>

    @Headers("Content-Type: application/json")
    @POST("subscription/recover")
    fun recoverSubscription(
        @Header("Authorization") authorization: String,
        @Body payment: Payment
    ): Call<ProductsAndPayments>

    @Headers("Content-Type: application/json")
    @POST("payments")
    fun getUserPayments(
        @Header("Authorization") authorization: String,
        @Body paymentsListRequest: PaymentsListRequest
    ): Call<List<Payment>>

    @Headers("Content-Type: application/json")
    @POST("payment/delete")
    fun deletePayment(
        @Header("Authorization") authorization: String,
        @Body payment: Payment
    ): Call<ResponseBody>
}