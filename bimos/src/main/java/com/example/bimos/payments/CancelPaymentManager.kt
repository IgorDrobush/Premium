package com.example.bimos.payments

import android.util.Log
import com.example.bimos.apiService.InitBimosApiService
import com.example.bimos.payments.models.*
import com.example.bimos.productsAndPayments.models.ProductsAndPayments
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelPaymentManager {

    private val productApiService = InitBimosApiService.init()

    fun cancelPaymentRequest(
        payment: Payment,
        cancellationPaymentCallback: () -> Unit,
        successfulCancelCallback: (ProductsAndPayments) -> Unit,
        yookassaErrorCancelCallback: (YookassaError) -> Unit,
        errorCancelCallback: (Int, String, String?) -> Unit,
        failureCancelCallback: (Call<CancelPaymentResponse>, Throwable) -> Unit,
        cancelCallback: (ProductsAndPayments) -> Unit,
    ) {

        Log.d("Log1", "Сработала CancelPaymentManager.cancelPaymentRequest() в библиотеке Bimos")

        cancellationPaymentCallback()

        val call = productApiService.cancelPayment(payment.applicationId, payment)

        call.enqueue(object : Callback<CancelPaymentResponse> {
            override fun onResponse(call: Call<CancelPaymentResponse>, response: Response<CancelPaymentResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val productsAndPayments = it.productsAndPayments
                        val yookassaError = it.yookassaError

                        successfulCancelCallback(productsAndPayments)
                        cancelCallback(productsAndPayments)

                        if(yookassaError != null) {
                            yookassaErrorCancelCallback(yookassaError)
                        }
                    } ?: run {
                        errorCancelCallback(
                            response.code(),
                            response.message(),
                            "Ответ от сервера null, либо ошибка парсинга ответа"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val statusCode = response.code()
                    val statusMessage = response.message()
                    errorCancelCallback(statusCode, statusMessage, errorBody)
                }
            }

            override fun onFailure(call: Call<CancelPaymentResponse>, t: Throwable) {
                failureCancelCallback(call, t)
            }
        })
    }
}