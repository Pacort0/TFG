package com.example.regalanavidad.sharedScreens

import android.util.Log
import com.example.regalanavidad.modelos.DonacionResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

suspend fun getDataFromGoogleSheet(spreadsheetId: String, sheetName: String): DonacionResponse {
    return withContext(Dispatchers.IO) {
        val url = "https://script.google.com/macros/s/AKfycbyzekY3d0lcbUuFSBrYVQeTNnDhgANMZomqFwS33qn92gEqyeMM_3VTGg1aoqD_4hnLwA/exec?spreadsheetId=$spreadsheetId&sheet=$sheetName"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        try {
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()
            if (responseData != null) {
                Log.d("JSON", responseData)
            }
            val donaciones: DonacionResponse = Gson().fromJson(responseData, DonacionResponse::class.java)
            donaciones
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            e.message?.let { Log.e("JSON", it) }
            DonacionResponse(emptyList())
        }
    }
}