package com.example.bimos.payments

import android.util.Log
import com.example.bimos.apiService.InitBimosApiService
import com.example.bimos.payments.models.Payment
import com.example.bimos.payments.models.PaymentResponse
import com.example.bimos.payments.models.PaymentStatusRequest
import com.example.bimos.payments.models.YookassaError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentStatusManager {

    private val productApiService = InitBimosApiService.init()

    fun paymentStatusRequest(
        paymentStatusRequest: PaymentStatusRequest,
        applicationId: String,
        successfulStatusResponse: (Payment) -> Unit,
        yookassaErrorStatusCallback: (YookassaError) -> Unit,
        errorStatusCallback: (Int, String, String?) -> Unit,
        failureStatusCallback: (Call<PaymentResponse>, Throwable) -> Unit,
        callbackRewritePayment: (Payment) -> Unit
    ) {
        Log.d("Log1", "Сработала PaymentStatusManager.getPaymentStatus() в библиотеке Bimos")

        val call = productApiService.checkPaymentStatus(applicationId, paymentStatusRequest)

        call.enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val payment = it.payment
                        val yookassaError = it.yookassaError

                        if(payment != null) {
                            successfulStatusResponse(payment)
                            if (payment.status == "succeeded" || payment.status == "waiting_for_capture") {
                                callbackRewritePayment(payment)
                            }
                        }

                        if(yookassaError != null) {
                            yookassaErrorStatusCallback(yookassaError)
                        }
                    } ?: run {
                        errorStatusCallback(
                            response.code(),
                            response.message(),
                            "Ответ от сервера null, либо ошибка парсинга ответа"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val statusCode = response.code()
                    val statusMessage = response.message()
                    errorStatusCallback(statusCode, statusMessage, errorBody)
                }
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                failureStatusCallback(call, t)
            }
        })
    }
}