package com.example.regalanavidad.apiRouteService

import com.google.gson.annotations.SerializedName
data class RouteResponse(@SerializedName("features") val features:List<Feature>)
data class Feature(
    @SerializedName("properties") val properties: Properties,
    @SerializedName("geometry") val geometry: Geometry
)
data class Geometry(@SerializedName("coordinates")val coordinates:List<List<Double>>)
data class Properties(@SerializedName("segments") val segments: List<Segment>)
data class Segment(@SerializedName("duration") val duration: Double)