package com.example.regalanavidad.modelos

import com.google.gson.annotations.SerializedName

data class Feature(@SerializedName("geometry")val geometry:Geometry)
data class Geometry(@SerializedName("coordinates")val coordinates:List<List<Double>>)