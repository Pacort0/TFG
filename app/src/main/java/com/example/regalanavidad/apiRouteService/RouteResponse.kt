package com.example.regalanavidad.apiRouteService

import com.example.regalanavidad.apiRouteService.Feature
import com.google.gson.annotations.SerializedName

data class RouteResponse(@SerializedName("features") val features:List<Feature>)
