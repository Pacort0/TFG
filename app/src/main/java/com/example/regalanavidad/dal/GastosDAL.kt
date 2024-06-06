package com.example.regalanavidad.dal

import android.util.Log
import com.example.regalanavidad.modelos.Gasto
import com.example.regalanavidad.modelos.GastoResponse
import com.example.regalanavidad.modelos.RequestPostGasto
import com.example.regalanavidad.organizadorScreens.gastosSpreadsheetId
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

suspend fun getGastosFromSheet(): GastoResponse {
    return withContext(Dispatchers.IO) {
        val url = "https://script.google.com/macros/s/AKfycbz1Iog-YaDAVO9BigW6OxVqZ5bsG9EmCSX4mqfJmT719IRh0MMtUOz7xNQeVjbhyXr4/exec?spreadsheetId=$gastosSpreadsheetId&sheet=Gastos"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        try {
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()
            if (responseData != null) {
                Log.d("JSON", responseData)
            }
            val gastos: GastoResponse = Gson().fromJson(responseData, GastoResponse::class.java)
            gastos
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            e.message?.let { Log.e("JSON", it) }
            GastoResponse("", emptyList())
        }
    }
}

suspend fun updateGastosDataInGoogleSheet(gasto: Gasto): Response {
    return withContext(Dispatchers.IO) {
        val url = "https://script.google.com/macros/s/AKfycbw7dh0NQDsWf9ptmeiTtFJc4hhatCI06bboCCfCYPuK2537l5LdUf3He2o7cQDNEV69/exec"
        val requestPost = RequestPostGasto(gastosSpreadsheetId, "Gastos", gasto)
        val json = Gson().toJson(requestPost)
        Log.d("postGastos", json)
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        try {
            val response = client.newCall(request).execute()
            response
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let { Log.e("JSON", it) }
            Response.Builder().code(500).message("Error al actualizar los datos").build()
        }
    }
}