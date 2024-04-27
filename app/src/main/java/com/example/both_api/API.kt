package com.example.both_api

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.converter.gson.GsonConverterFactory

const val Base_URL = "https://nodei.ssccglpinnacle.com/"

// Define a data class to represent the JSON response
data class KeyResponse(val key: String)

interface API_1 {
    @GET("getKey/{value}")
    suspend fun getKeyValue(@Path("value") value: String): KeyResponse
}

// Create a function to build the Retrofit instance
fun createAPI(): API_1 {
    val retrofit = Retrofit.Builder()
        .baseUrl(Base_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(API_1::class.java)
}

data class BarcodeRequest(val searchValue: String?)

// Define a data class to represent the JSON response
data class BarcodeResponse(val result: String)

interface API_2 {
    @POST("searchBarr1")
    suspend fun searchBarcode(@Body searchValue: BarcodeRequest): BarcodeResponse
}

// Create a function to build the Retrofit instance for API_2
fun createAPI2(): API_2 {
    val retrofit = Retrofit.Builder()
        .baseUrl(Base_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(API_2::class.java)
}