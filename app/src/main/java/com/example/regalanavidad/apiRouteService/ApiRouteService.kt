package com.example.regalanavidad.apiRouteService

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRouteService {
    @GET("/v2/directions/foot-walking")
    suspend fun getWalkableRoute(
        @Query("api_key") key:String,
        @Query("start", encoded = true) start:String,
        @Query("end", encoded = true) end:String
    ): Response<RouteResponse>

    @GET("/v2/directions/driving-car")
    suspend fun getDrivingRoute(
        @Query("api_key") key:String,
        @Query("start", encoded = true) start:String,
        @Query("end", encoded = true) end:String
    ): Response<RouteResponse>
}