package com.example.bimos.payments

import com.example.bimos.apiService.InitBimosApiService
import com.example.bimos.payments.models.Payment
import com.example.bimos.payments.models.PaymentsListRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserPaymentsManager {

    private val productApiService = InitBimosApiService.init()

    fun userPaymentsRequest(
        userUid: String,
        applicationId: String,
        successCallback: (List<Payment>) -> Unit,
        errorCallback: (Int, String, String?) -> Unit,
        failureCallback: (Call<List<Payment>>, Throwable) -> Unit,
        userPaymentsCallback: (List<Payment>) -> Unit
    ) {

        val paymentsListRequest = PaymentsListRequest(
            userUid = userUid,
            applicationId = applicationId
        )

        val call = productApiService.getUserPayments(
            authorization = applicationId,
            paymentsListRequest = paymentsListRequest
        )

        call.enqueue(object : Callback<List<Payment>> {
            override fun onResponse(call: Call<List<Payment>>, response: Response<List<Payment>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        successCallback(it)
                        userPaymentsCallback(it)
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

            override fun onFailure(call: Call<List<Payment>>, t: Throwable) {
                failureCallback(call, t)
//                callback(null)
            }
        })
    }
}