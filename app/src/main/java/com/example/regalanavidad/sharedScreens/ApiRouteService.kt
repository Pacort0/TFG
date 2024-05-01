package com.example.regalanavidad.sharedScreens

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRouteService {
    @GET("/v2/directions/driving-car")
    fun getRoute(
        @Query("api_key") key:String,
                 @Query("start") start:String,
                 @Query("end") end:String
    ): Response<*>
}