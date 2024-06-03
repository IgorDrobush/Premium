package com.example.bimos.apiService

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object InitBimosApiService {

    private lateinit var bimosApiService: BimosApiService

    fun init(): BimosApiService {
        val gson = GsonBuilder()
            .serializeNulls()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.104:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        bimosApiService = retrofit.create(BimosApiService::class.java)

        return bimosApiService
    }
}