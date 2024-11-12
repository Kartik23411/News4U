package com.example.kit.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface network {

    @GET("v4/search")
    fun getNews(
        @Query("q") q:String,
        @Query("lang") lang:String,
        @Query("country") country:String,
        @Query("sortby") sortby:String,
        @Query("max") max: Int,
        @Query("apikey") apikey: String
    ):Call<Response>
}