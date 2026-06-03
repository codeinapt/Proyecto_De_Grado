package com.example.modulos.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class SyncUpRequest(
    val gateway_id: String,
    val batch_messages: List<Map<String, Any>>
)

data class SyncResponse(
    val status: String,
    val message: String
)

interface ApiService {
    @POST("/api/v1/sync/up")
    suspend fun syncUp(@Body request: SyncUpRequest): Response<SyncResponse>

    @GET("/api/v1/sync/down/{gw_id}")
    suspend fun syncDown(@Path("gw_id") gatewayId: String): Response<List<Map<String, Any>>>
}
