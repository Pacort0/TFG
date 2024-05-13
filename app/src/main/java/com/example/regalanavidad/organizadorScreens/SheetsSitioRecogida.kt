package com.example.regalanavidad.organizadorScreens

import android.util.Log
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.modelos.CentroEducativo
import com.example.regalanavidad.modelos.CentroEducativoResponse
import com.example.regalanavidad.sharedScreens.usuario
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

var informacionCentrosRecogida = mutableStateOf(emptyList<CentroEducativo>())
const val infoCentrosSheetId = "1RtpW4liafATG-CW-tFozrFZzM-8tzg9e_KIj-9DT4gA"
var listaEstadosCentrosCambiados = mutableStateOf(emptyList<CentroEducativo>())
var centroEducativoElegido = CentroEducativo()

@Composable
fun ExcelScreen(navController: NavController){
    when(usuario.nombreRango){
        "Coordinador" -> {
            PaginaSheetCentrosEducativos(navController)
        }
        "Secretaría" -> {
            Text(text = "Hola secretari@")
        }
        "Tesorería" -> {
            Text(text = "Hola tesorer@")
        }
        "RR.II." -> {
            PaginaSheetCentrosEducativos(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadosSubMenu(drawerState: DrawerState, scope: CoroutineScope, centroEducativo: CentroEducativo){
    val opcionesEstados = listOf("No iniciada", "En curso", "En revisión", "Completada", "Cancelada")
    var expanded by remember { mutableStateOf(false) }
    var nuevoEstado by remember { mutableStateOf(centroEducativo.estadoCentro) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = nuevoEstado,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            opcionesEstados.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, fontSize = 18.sp) },
                    onClick = {
                        if(selectionOption != centroEducativo.estadoCentro){
                            nuevoEstado = selectionOption
                            //centroEducativo.estadoCentro = selectionOption
                            //listaEstadosCentrosCambiados.value += centro
                        }
                        expanded = false
                        scope.launch { drawerState.close() }
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
suspend fun getCentrosFromDistrito(distritoSeleccionado:String):List<CentroEducativo>{
    var centroResponse = CentroEducativoResponse(emptyList())
    when(distritoSeleccionado){
        "Sevilla Este" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "SevillaEste")
        }
        "Aljarafe" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "Aljarafe")
        }
        "Montequinto" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "Montequinto")
        }
        "Casco Antiguo" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "CascoAntiguo")
        }
        "Nervión-Porvenir" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "NervionPorvenir")
        }
        "Triana" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "Triana")
        }
        "Heliópolis" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "Heliopolis")
        }
        "Facultades US" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "FacultadesUS")
        }
    }
    Log.d("Centros", centroResponse.toString())
    return centroResponse.centros
}

suspend fun getCentrosDataFromGoogleSheet(spreadsheetId: String, sheetName: String): CentroEducativoResponse {
    return withContext(Dispatchers.IO) {
        val url = "https://script.google.com/macros/s/AKfycbysv005twqlh_O5NdBkZD_MW7jmM1pByco38U0hwpgzzOVz-1wmiFFaLCnr2Ri0I-0z0A/exec?spreadsheetId=$spreadsheetId&sheet=$sheetName"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        try {
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()
            if (responseData != null) {
                Log.d("JSON", responseData)
            }
            val centros: CentroEducativoResponse = Gson().fromJson(responseData, CentroEducativoResponse::class.java)
            centros
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            e.message?.let { Log.e("JSON", it) }
            CentroEducativoResponse(emptyList())
        }
    }
}