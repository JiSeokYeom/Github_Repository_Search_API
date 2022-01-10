package com.example.afreecatv.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofitClient : Retrofit? = null

    fun getClient(baseUrl : String): Retrofit?{
         retrofitClient = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
   return retrofitClient
    }
}