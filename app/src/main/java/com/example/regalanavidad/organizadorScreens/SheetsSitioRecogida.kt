package com.example.regalanavidad.organizadorScreens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcelScreen(navController: NavController){
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    when(usuario.nombreRango){
        "Coordinador" -> {

        }
        "Secretaría" -> {
            Text(text = "Hola secretari@")
        }
        "Tesorería" -> {
            Text(text = "Hola tesorer@")
        }
        "RR.II." -> {
            var expanded by remember { mutableStateOf(false) }
            val opcionesDistritos = listOf("Sevilla Este", "Aljarafe", "Montequinto", "Casco Antiguo", "Nervión-Porvenir", "Triana", "Heliópolis", "Facultades US")
            var distritoSeleccionado by remember { mutableStateOf(opcionesDistritos[3]) }
            var centrosLoading by remember {mutableStateOf(true)}

            LaunchedEffect(key1 = centrosLoading) {
                informacionCentrosRecogida.value = getCentrosFromDistrito(distritoSeleccionado = distritoSeleccionado)
                centrosLoading = false
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopStart)) {
                Column {
                    Text(
                        text = "Selecciona el distrito",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 18.sp
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = distritoSeleccionado,
                            onValueChange = {},
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            opcionesDistritos.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption, fontSize = 18.sp) },
                                    onClick = {
                                        distritoSeleccionado = selectionOption
                                        expanded = false
                                        centrosLoading = true
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                    if(informacionCentrosRecogida.value.isNotEmpty() && !centrosLoading){
                        LazyColumn {
                            items(informacionCentrosRecogida.value.size) { index ->
                                Card (
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillParentMaxWidth()
                                        .height(60.dp)
                                        .clickable {
                                            centroEducativoElegido = informacionCentrosRecogida.value[index]
                                            navController.navigate("PagContactosCentrosEdu")
                                        }
                                ) {
                                    Row (horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically){
                                        Column (Modifier.weight(0.55f), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                                            Text(text = informacionCentrosRecogida.value[index].nombreCentro)
                                        }
                                        Column (Modifier.weight(0.45f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                                            EstadosSubMenu(drawerState, scope, informacionCentrosRecogida.value[index])
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        CircularProgressIndicator()
                        Text(
                            text = "Cargando centros...",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
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