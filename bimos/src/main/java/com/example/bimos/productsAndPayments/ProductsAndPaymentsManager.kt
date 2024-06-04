package com.example.bimos.productsAndPayments

import com.example.bimos.apiService.InitBimosApiService
import com.example.bimos.payments.models.PaymentsListRequest
import com.example.bimos.productsAndPayments.models.ProductsAndPayments
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsAndPaymentsManager {

    private val productApiService = InitBimosApiService.init()

    fun productsAndPaymentsRequest(
        paymentsListRequest: PaymentsListRequest,
        successCallback: (ProductsAndPayments) -> Unit,
        errorCallback: (Int, String, String?) -> Unit,
        failureCallback: (Call<ProductsAndPayments>, Throwable) -> Unit,
        productsAndPaymentsCallback: (ProductsAndPayments) -> Unit
    ) {
        val call = productApiService.getAllProductsAndPayments(
            authorization = paymentsListRequest.applicationId,
            paymentsListRequest = paymentsListRequest
        )

        call.enqueue(object : Callback<ProductsAndPayments> {
            override fun onResponse(call: Call<ProductsAndPayments>, response: Response<ProductsAndPayments>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        successCallback(it)
                        productsAndPaymentsCallback(it)
                    } ?: run {
                        errorCallback(
                            response.code(),
                            response.message(),
                            "Ответ от сервера null, либо ошибка парсинга ответа"
                        )
//                        callback(null)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val statusCode = response.code()
                    val statusMessage = response.message()
                    errorCallback(statusCode, statusMessage, errorBody)
//                    callback(null)
                }
            }

            override fun onFailure(call: Call<ProductsAndPayments>, t: Throwable) {
                failureCallback(call, t)
//                callback(null)
            }
        })
    }
}


