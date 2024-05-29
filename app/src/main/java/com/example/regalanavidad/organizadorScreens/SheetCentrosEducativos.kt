package com.example.regalanavidad.organizadorScreens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.CentroEducativo
import com.example.regalanavidad.modelos.CentroEducativoRequest
import com.example.regalanavidad.modelos.CentroEducativoResponse
import com.example.regalanavidad.modelos.RequestPostCentroEducativo
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

private var listaCentrosEducativos = mutableStateOf(emptyList<CentroEducativo>())
const val infoCentrosSheetId = "1RtpW4liafATG-CW-tFozrFZzM-8tzg9e_KIj-9DT4gA"
private var listaEstadosCentrosCambiados = mutableStateOf(emptyList<CentroEducativoRequest>())
var centroEducativoElegido = CentroEducativo()
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PaginaSheetCentrosEducativos(navController: NavController, onMapaCambiado: (Boolean) -> Unit) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var expanded by remember { mutableStateOf(false) }
    val opcionesDistritos = listOf("Sevilla Este", "Aljarafe", "Montequinto", "Casco Antiguo", "Nervión-Porvenir", "Triana", "Heliópolis", "Facultades US")
    var distritoSeleccionado by remember { mutableStateOf(opcionesDistritos[3]) }
    var centrosLoading by remember { mutableStateOf(true) }
    var showAlertDialog by remember { mutableStateOf(false)}
    var opcionSeleccionada by remember {mutableStateOf("")}
    var llamadaBackHandler by remember { mutableStateOf(false) }
    var navegaContactoCentro by remember { mutableStateOf(false) }
    var indexActual = 0
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(refreshing = centrosLoading, onRefresh = {centrosLoading = !centrosLoading})

    LaunchedEffect(key1 = centrosLoading) {
        onMapaCambiado(true)
        listaCentrosEducativos.value = getCentrosFromDistrito(distritoSeleccionado = distritoSeleccionado)
        centrosLoading = false
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
        .pullRefresh(pullRefreshState)
    ) {
        Column {
            Row (
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    Modifier.weight(0.5f)
                ) {
                    Text(
                        text = "Selecciona el distrito",
                        fontSize = 18.sp
                    )
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                ) {
                    if (listaEstadosCentrosCambiados.value.isNotEmpty()) {
                        IconButton(onClick = {
                            Toast.makeText(context, "Actualizando centros...", Toast.LENGTH_SHORT)
                                .show()
                            scope.launch(Dispatchers.IO) {
                                updateCentrosDataInGoogleSheet(
                                    infoCentrosSheetId,
                                    cambiaNombreDistrito(distritoSeleccionado),
                                    listaEstadosCentrosCambiados.value
                                )
                                listaEstadosCentrosCambiados.value = emptyList()
                                centrosLoading = true
                            }
                        }, modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.save),
                                    contentDescription = "Guardar cambios",
                                    Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Guardar cambios")
                            }
                        }
                    }
                }
            }
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
                                if(listaEstadosCentrosCambiados.value.isNotEmpty()){
                                    showAlertDialog = true
                                    opcionSeleccionada = selectionOption
                                } else {
                                    distritoSeleccionado = selectionOption
                                    expanded = false
                                    centrosLoading = true
                                }
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            if(listaCentrosEducativos.value.isNotEmpty() && !centrosLoading){
                LazyColumn {
                    items(listaCentrosEducativos.value.size) { index ->
                        Card (
                            modifier = Modifier
                                .padding(8.dp)
                                .fillParentMaxWidth()
                                .height(80.dp)
                                .clickable {
                                    indexActual = index
                                    showAlertDialog = true
                                    navegaContactoCentro = true
                                }
                        ) {
                            Row (horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically){
                                Column (Modifier.weight(0.55f).fillParentMaxHeight(), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                                    Text(text = listaCentrosEducativos.value[index].nombreCentro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(text = listaCentrosEducativos.value[index].tareaCentro, fontSize = 18.sp)
                                }
                                Column (Modifier.weight(0.45f).fillParentMaxHeight(), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                                    EstadosSubMenu(drawerState, scope, listaCentrosEducativos.value[index])
                                }
                            }
                        }
                    }
                }
            } else {
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Cargando centros...",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        if(showAlertDialog){
            AlertDialog(
                onDismissRequest = {
                    showAlertDialog = false
                },
                title = {
                    Text(text = "Tiene cambios sin guardar")
                },
                text = {
                    Text("Si sale de la página sin guardar los cambios, perderá la información modificada.\n¿Está seguro de querer continuar?")
                },
                confirmButton = {
                    if(llamadaBackHandler){
                        Button(
                            onClick = {
                                showAlertDialog = false
                                navController.popBackStack()
                            }
                        ){
                            Text("Sí, estoy seguro")
                        }
                    } else if (navegaContactoCentro){
                        showAlertDialog = false
                        centroEducativoElegido = listaCentrosEducativos.value[indexActual]
                        navController.navigate("PagContactosCentrosEdu")
                    }
                    else {
                        Button(
                            onClick = {
                                showAlertDialog = false
                                distritoSeleccionado = opcionSeleccionada
                                expanded = false
                                centrosLoading = true
                                listaEstadosCentrosCambiados.value = emptyList()
                            }
                        ) {
                            Text("Sí, estoy seguro")
                        }
                    }
                },
                dismissButton = {
                    if (llamadaBackHandler){
                        Button(
                            onClick = {
                                showAlertDialog = false
                                llamadaBackHandler = false
                            }
                        ){
                            Text(text = "No")
                        }
                    } else {
                        Button(
                            onClick = {
                                showAlertDialog = false
                            }
                        ) {
                            Text("No")
                        }
                    }
                }
            )
        }
        BackHandler {
            if(listaEstadosCentrosCambiados.value.isNotEmpty()){
                llamadaBackHandler = true
                showAlertDialog = true
            } else {
                navController.popBackStack()
            }
        }
        PullRefreshIndicator(
            refreshing = centrosLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
fun cambiaNombreDistrito(sheetName: String):String{
    var nuevoSheetName = ""
    when (sheetName){
        "Sevilla Este" -> {
            nuevoSheetName = "SevillaEste"
        }
        "Aljarafe" -> {
            nuevoSheetName = "Aljarafe"
        }
        "Montequinto" -> {
            nuevoSheetName =  "Montequinto"
        }
        "Casco Antiguo" -> {
            nuevoSheetName =  "CascoAntiguo"
        }
        "Nervión-Porvenir" -> {
            nuevoSheetName =  "NervionPorvenir"
        }
        "Triana" -> {
            nuevoSheetName =  "Triana"
        }
        "Heliópolis" -> {
            nuevoSheetName =  "Heliopolis"
        }
        "Facultades US" -> {
            nuevoSheetName = "FacultadesUS"
        }
    }
    return nuevoSheetName
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadosSubMenu(drawerState: DrawerState, scope: CoroutineScope, centroEducativo: CentroEducativo){
    val opcionesEstados = listOf("No Iniciada", "En curso", "En revisión", "Completada", "Cancelada")
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
                            centroEducativo.estadoCentro = selectionOption

                            // Buscar si ya existe un registro para este centro
                            val index = listaEstadosCentrosCambiados.value.indexOfFirst { it.nombreCentro == centroEducativo.nombreCentro }

                            if (index != -1) {
                                // Si existe, sobrescribirlo
                                val newList = listaEstadosCentrosCambiados.value.toMutableList()
                                newList[index] = centroEducativo.toCentroEducativoRequest()
                                listaEstadosCentrosCambiados.value = newList
                            } else {
                                // Si no existe, añadirlo a la lista
                                listaEstadosCentrosCambiados.value += centroEducativo.toCentroEducativoRequest()
                            }
                        }
                        expanded = false
                        scope.launch { drawerState.close() }
                        Log.d("Centros", listaEstadosCentrosCambiados.value.toString())
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
        val url = "https://script.google.com/macros/s/AKfycbygcfd8kcWN8C0fJw3Eh4vW15BhQ1GVu6cHw1MjO9rbe5bWgxxIjhk12SVGWenap40FPA/exec?spreadsheetId=$spreadsheetId&sheet=$sheetName"
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

suspend fun updateCentrosDataInGoogleSheet(spreadsheetId: String, sheetName: String, centros: List<CentroEducativoRequest>): Response {
    return withContext(Dispatchers.IO) {
        val url = "https://script.google.com/macros/s/AKfycbygcfd8kcWN8C0fJw3Eh4vW15BhQ1GVu6cHw1MjO9rbe5bWgxxIjhk12SVGWenap40FPA/exec"
        val requestPost = RequestPostCentroEducativo(spreadsheetId, sheetName, centros)
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