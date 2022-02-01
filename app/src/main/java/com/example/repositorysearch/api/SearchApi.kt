package com.example.repositorysearch.api

import com.example.repositorysearch.MainRvData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("/search/repositories")
    fun searchRepositories(@Query("q") q: String,
                           @Query("per_page") per_page : Int,
                           @Query("page") page : Int
    ) : Call<MainRvData>
}