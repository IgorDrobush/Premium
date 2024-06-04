package com.example.bimos.payments

import android.util.Log
import com.example.bimos.apiService.InitBimosApiService
import com.example.bimos.payments.models.*
import com.example.bimos.products.models.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreatePaymentManager {

    private val productApiService = InitBimosApiService.init()

    fun createPaymentRequest(
        product: Product,
        initParameters: InitParameters,
        paymentToken: String,
        creatingPaymentCallback: () -> Unit,
        successfulCreateCallback: (Payment) -> Unit,
        yookassaErrorCreateCallback: (YookassaError) -> Unit,
        errorCreateCallback: (Int, String, String?) -> Unit,
        failureCreateCallback: (Call<PaymentResponse>, Throwable) -> Unit,
        callback: (Payment) -> Unit,
        callbackAddPayment: (Payment) -> Unit
    ) {

        Log.d("Log1", "Сработала CreatePaymentManager.createUserPayment() в библиотеке Bimos")

        val idempotenceKey = UUID.randomUUID().toString()

        val paymentRequest = PaymentRequest(
            userUid = initParameters.userUid,
            productId = product.id,
            applicationId = initParameters.applicationId,
            paymentToken = paymentToken,
            paymentMethodType = "",
            returnUrl = null,
            value = product.price.toDouble().toString(),
            currency = "RUB",
            shopId = initParameters.shopId,
            secretKey = initParameters.secretKey,
            idempotenceKey = idempotenceKey,
            description = "${initParameters.applicationName}, ${product.name}, ${initParameters.userUid}",
            productType = product.type,
            trialPeriod = product.trialPeriod,
            subscriptionPeriod = product.subscriptionPeriod
        )

        val authorization = initParameters.applicationId

        creatingPaymentCallback()

        val call = productApiService.createPayment(authorization, paymentRequest)

        call.enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val payment = it.payment
                        val yookassaError = it.yookassaError

                        if(payment != null) {

                            if (payment.status == "pending") {
                                Log.d("Log1", "status pending в библиотеке Bimos")
                                callback(payment)
                            } else {
                                Log.d("Log1", "status != pending в библиотеке Bimos")
                                successfulCreateCallback(payment)
                                if (payment.status == "succeeded" || payment.status == "waiting_for_capture") {
                                    callbackAddPayment(payment)
                                }
                            }
                        }

                        if(yookassaError != null) {
                            yookassaErrorCreateCallback(yookassaError)
                        }
                    } ?: run {
                        errorCreateCallback(
                            response.code(),
                            response.message(),
                            "Ответ от сервера null, либо ошибка парсинга ответа"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val statusCode = response.code()
                    val statusMessage = response.message()
                    errorCreateCallback(statusCode, statusMessage, errorBody)
                }
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                failureCreateCallback(call, t)
            }
        })
    }
}