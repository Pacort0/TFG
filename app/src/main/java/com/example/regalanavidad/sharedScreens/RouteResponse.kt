package com.example.regalanavidad.sharedScreens

import com.example.regalanavidad.modelos.Feature
import com.google.gson.annotations.SerializedName

data class RouteResponse(@SerializedName("features") val features:List<Feature>)
