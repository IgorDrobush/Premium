package com.example.bimos.payments

import com.example.bimos.apiService.InitBimosApiService
import com.example.bimos.payments.models.Payment
import com.example.bimos.productsAndPayments.models.ProductsAndPayments
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecoverSubscriptionManager {

    private val productApiService = InitBimosApiService.init()

    fun recoverSubscriptionRequest(
        payment: Payment,
        successRecoverSubscriptionCallback: (ProductsAndPayments) -> Unit,
        errorRecoverSubscriptionCallback: (Int, String, String?) -> Unit,
        failureRecoverSubscriptionCallback: (Call<ProductsAndPayments>, Throwable) -> Unit,
        recoverSubscriptionCallback: (ProductsAndPayments) -> Unit
    ) {
        val call = productApiService.recoverSubscription(
            authorization = payment.applicationId,
            payment = payment
        )

        call.enqueue(object : Callback<ProductsAndPayments> {
            override fun onResponse(call: Call<ProductsAndPayments>, response: Response<ProductsAndPayments>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        successRecoverSubscriptionCallback(it)
                        recoverSubscriptionCallback(it)
                    } ?: run {
                        errorRecoverSubscriptionCallback(
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
                    errorRecoverSubscriptionCallback(statusCode, statusMessage, errorBody)
//                    cancelSubscriptionCallback(null)
                }
            }

            override fun onFailure(call: Call<ProductsAndPayments>, t: Throwable) {
                failureRecoverSubscriptionCallback(call, t)
//                cancelSubscriptionCallback(null)
            }
        })
    }
}