package com.plcoding.wearosstopwatch.presentation.api

import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("api-token-auth/")
    @Headers("Content-Type: application/json")
    fun getToken(@Body body: JsonObject): Call<JsonObject>

    @POST("api/watch/")
    @Headers("Content-Type: application/json")
    fun testPost(@Body body: JSONObject, @Header("Authorization") token: String): Call<Unit>

}
