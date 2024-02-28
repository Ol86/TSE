package com.plcoding.wearosstopwatch.presentation

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface PostApiService {
    @POST("/post")
    @Headers("Content-Type: application/json")
    fun postData(@Body body: JSONObject, @Header("Authorization") token: String): Call<Unit>
}