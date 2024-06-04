package com.example.bimos.products

import com.example.bimos.apiService.InitBimosApiService
import com.example.bimos.products.models.Product
import com.example.bimos.products.models.ApplicationProductsRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApplicationProductsManager {

    private val productApiService = InitBimosApiService.init()

    fun applicationProductsRequest(
        applicationId: String,
        successCallback: (List<Product>) -> Unit,
        errorCallback: (Int, String, String?) -> Unit,
        failureCallback: (Call<List<Product>>, Throwable) -> Unit,
        applicationProductsCallback: (List<Product>) -> Unit
    ) {

        val applicationProductsRequest = ApplicationProductsRequest(
            applicationId = applicationId
        )

        val call = productApiService.getApplicationProducts(
            authorization = applicationId,
            applicationProductsRequest = applicationProductsRequest
        )

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        successCallback(it)
                        applicationProductsCallback(it)
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

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                failureCallback(call, t)
//                callback(null)
            }
        })
    }
}