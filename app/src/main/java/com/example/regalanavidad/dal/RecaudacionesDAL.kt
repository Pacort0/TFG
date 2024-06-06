package com.example.regalanavidad.dal

import android.util.Log
import com.example.regalanavidad.modelos.Producto
import com.example.regalanavidad.modelos.ProductoResponse
import com.example.regalanavidad.modelos.RequestPostRecaudacion
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

suspend fun getRecaudacionesFromSheet(spreadsheetId:String, sheet:String): ProductoResponse {
    return withContext(Dispatchers.IO) {
        val url = "https://script.google.com/macros/s/AKfycbzUqhk7RMnzqDM0m8BzvkcK865jTDQpdVSkVPaqKLW1TQ67d_kWZtC4DbqFTRxC2ZNL/exec?spreadsheetId=$spreadsheetId&sheet=$sheet"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        try {
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()
            if (responseData != null) {
                Log.d("JSON", responseData)
            }
            val productos: ProductoResponse = Gson().fromJson(responseData, ProductoResponse::class.java)
            productos
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            e.message?.let { Log.e("JSON", it) }
            ProductoResponse(emptyList())
        }
    }
}
suspend fun updateProdDataInGoogleSheet(spreadsheetId: String, sheetName: String, productos: List<Producto>): Response {
    return withContext(Dispatchers.IO) {
        val url = "https://script.google.com/macros/s/AKfycbyfOxSJzYvMDg4qXHy3AXTXXgI_YWJJIfrB5TBoX3jrnhk8O0L-qiHAuQ3PthduUyxW/exec"
        val requestPost = RequestPostRecaudacion(spreadsheetId, sheetName, productos)
        val json = Gson().toJson(requestPost)
        Log.d("postCentros", json)
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