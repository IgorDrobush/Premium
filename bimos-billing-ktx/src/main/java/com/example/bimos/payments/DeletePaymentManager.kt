package com.example.bimos.payments

import android.util.Log
import com.example.bimos.apiService.InitBimosApiService
import com.example.bimos.payments.models.Payment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeletePaymentManager {

    private val productApiService = InitBimosApiService.init()

    fun deletePaymentRequest(payment: Payment) {

        val call = productApiService.deletePayment(payment.applicationId, payment)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("Log1", "Ответ response: $response")
                    val responseBody = response.body()?.string()
                    if(responseBody != null) {
                        Log.d("Log1", "Ответ сервера DeletePayment: $responseBody")
                    } else {
                        Log.d("Log1", "Ответа нет DeletePayment")
                    }
                } else {
                    Log.d("Log1", "Error creating payment DeletePayment: $response")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Log1", "Network request failed with error DeletePayment: ${t.message}")
            }
        })
    }
}