package com.example.afreecatv

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SearchService {
    @GET("/search/repositories")
    fun searchRepositories(@Query("q") q: String) : Call<MainRvData>
}