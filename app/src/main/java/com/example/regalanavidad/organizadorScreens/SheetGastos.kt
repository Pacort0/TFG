package com.example.regalanavidad.organizadorScreens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.regalanavidad.modelos.Gasto
import com.example.regalanavidad.modelos.GastoResponse
import com.example.regalanavidad.modelos.RequestPostGasto
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val gastosSpreadsheetId = "1zffZhulQGscbwVZrEapV_DIt57aVyWDTauQqYJCVVuE"
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PaginaSheetGastos() {
    var listaGastos by remember { mutableStateOf(emptyList<Gasto>()) }
    var recargarGastos by remember { mutableStateOf(true) }
    var showGastoDialog by remember { mutableStateOf(false) }
    var motivoGasto by remember { mutableStateOf("") }
    var cantidadGasto by remember { mutableStateOf("") }
    var fechaEscogida by remember{mutableStateOf(LocalDate.now()) }
    val fechaFormateada by remember{ derivedStateOf { DateTimeFormatter.ofPattern("dd/MM/yyyy").format(fechaEscogida) } }
    var pagadoPor by remember { mutableStateOf("") }
    val fechaDialogState = rememberMaterialDialogState()
    var gastoResponse: GastoResponse
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(key1 = recargarGastos) {
        gastoResponse = getGastosFromSheet()
        listaGastos = gastoResponse.gastos
        recargarGastos = false
    }
    if (showGastoDialog){
        Dialog(onDismissRequest = {showGastoDialog = false}) {
            Box(modifier = Modifier
                .width(350.dp)
                .height(425.dp)
                .padding(35.dp)
                .background(Color.LightGray)
                .clip(RoundedCornerShape(20.dp))) {
                Column(
                    Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = motivoGasto,
                        onValueChange = { motivoGasto = it },
                        label = {
                            Text(
                                text = "Motivo del gasto"
                            )
                        })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cantidadGasto,
                        onValueChange = { cantidadGasto = it },
                        label = {
                            Text(
                                text = "Cantidad del gasto"
                            )
                        })
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Text(
                            text = "Fecha del gasto",
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = fechaFormateada,
                            modifier = Modifier.clickable { fechaDialogState.show() }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pagadoPor,
                        onValueChange = { pagadoPor = it },
                        label = {
                            Text(
                                text = "Pagado por"
                            )
                        })
                    Row(
                        Modifier
                            .weight(0.2f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(Modifier.weight(0.36f)) {}
                        Column(
                            Modifier
                                .weight(0.32f)
                                .clickable {
                                    showGastoDialog = false
                                }) {
                            Text(text = "CANCELAR", fontSize = 13.sp, color = Color.Magenta, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Column(
                            Modifier
                                .weight(0.32f)
                                .clickable {
                                    if (motivoGasto.isNotEmpty() && cantidadGasto.isNotEmpty() && pagadoPor.isNotEmpty()) {
                                        val gasto =
                                            Gasto(
                                                motivoGasto,
                                                fechaFormateada,
                                                cantidadGasto,
                                                pagadoPor
                                            )
                                        showGastoDialog = false
                                        scope.launch(Dispatchers.IO) {
                                            updateGastosDataInGoogleSheet(gasto)
                                            recargarGastos = true
                                        }
                                    } else {
                                        Toast.makeText(
                                                context,
                                                "Por favor, llena todos los campos",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                    }
                                }) {
                            Text(text = "GUARDAR", fontSize = 13.sp, color = Color.Magenta, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (listaGastos.isNotEmpty() && !recargarGastos) {
            LazyColumn {
                items(listaGastos.size) { index ->
                    GastoCard(gasto = listaGastos[index])
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Cargando gastos...",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        FloatingActionButton(
            onClick = {
                showGastoDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(0.dp, 0.dp, 14.dp, 14.dp)){
            Icon(Icons.Filled.Add, contentDescription = "Agregar gasto")
        }
        MaterialDialog(
            dialogState = fechaDialogState,
            buttons = {
                positiveButton("Guardar") {
                    fechaDialogState.hide()
                }
                negativeButton("Cancelar") {
                    fechaDialogState.hide()
                }
            }
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Selecciona la fecha del evento",
                onDateChange = { fechaEscogida = it },
                allowedDateValidator = { fecha ->
                    // Permitir solo fechas iguales o posteriores a la fecha actual
                    !fecha.isBefore(LocalDate.now())
                }
            )
        }
    }
}
@Composable
fun GastoCard(gasto: Gasto){
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                Modifier
                    .weight(0.4f)
                    .padding(0.4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = gasto.motivoGasto, fontSize = 14.sp)
            }
            Column (
                Modifier.weight(0.2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = gasto.fechaGasto, fontSize = 14.sp)
            }
            Column (
                Modifier.weight(0.2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = gasto.cantidadGasto, fontSize = 14.sp)
            }
            Column (
                Modifier.weight(0.2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = gasto.pagadoPor, fontSize = 14.sp)
            }
        }
    }
}

suspend fun getGastosFromSheet(): GastoResponse {
    return withContext(Dispatchers.IO) {
        val url = "https://script.google.com/macros/s/AKfycbw7dh0NQDsWf9ptmeiTtFJc4hhatCI06bboCCfCYPuK2537l5LdUf3He2o7cQDNEV69/exec?spreadsheetId=$gastosSpreadsheetId&sheet=Gastos"
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
            GastoResponse(emptyList())
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