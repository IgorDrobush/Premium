package com.example.bimos.payments

import com.example.bimos.apiService.InitBimosApiService
import com.example.bimos.payments.models.Payment
import com.example.bimos.productsAndPayments.models.ProductsAndPayments
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelSubscriptionManager {

    private val productApiService = InitBimosApiService.init()

    fun cancelSubscriptionRequest(
        payment: Payment,
        successCancelSubscriptionCallback: (ProductsAndPayments) -> Unit,
        errorCancelSubscriptionCallback: (Int, String, String?) -> Unit,
        failureCancelSubscriptionCallback: (Call<ProductsAndPayments>, Throwable) -> Unit,
        cancelSubscriptionCallback: (ProductsAndPayments) -> Unit
    ) {
        val call = productApiService.cancelSubscription(
            authorization = payment.applicationId,
            payment = payment
        )

        call.enqueue(object : Callback<ProductsAndPayments> {
            override fun onResponse(call: Call<ProductsAndPayments>, response: Response<ProductsAndPayments>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        successCancelSubscriptionCallback(it)
                        cancelSubscriptionCallback(it)
                    } ?: run {
                        errorCancelSubscriptionCallback(
                            response.code(),
                            response.message(),
                            "Ответ от сервера null, либо ошибка парсинга ответа"
                        )
//                        cancelSubscriptionCallback(null)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val statusCode = response.code()
                    val statusMessage = response.message()
                    errorCancelSubscriptionCallback(statusCode, statusMessage, errorBody)
//                    cancelSubscriptionCallback(null)
                }
            }

            override fun onFailure(call: Call<ProductsAndPayments>, t: Throwable) {
                failureCancelSubscriptionCallback(call, t)
//                cancelSubscriptionCallback(null)
            }
        })
    }
}