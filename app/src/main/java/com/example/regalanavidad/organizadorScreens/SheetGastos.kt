package com.example.regalanavidad.organizadorScreens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.Gasto
import com.example.regalanavidad.modelos.GastoResponse
import com.example.regalanavidad.modelos.RequestPostGasto
import com.example.regalanavidad.sharedScreens.dineroRecaudado
import com.example.regalanavidad.sharedScreens.donacionesSheetId
import com.example.regalanavidad.sharedScreens.getDonationDataFromGoogleSheet
import com.example.regalanavidad.sharedScreens.usuario
import com.example.regalanavidad.ui.theme.BordeIndvCards
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.example.regalanavidad.ui.theme.FondoTarjetaInception
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
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val gastosSpreadsheetId = "1zffZhulQGscbwVZrEapV_DIt57aVyWDTauQqYJCVVuE"
@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PaginaSheetGastos(onMapaCambiado: (Boolean) -> Unit) {
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
    val pullRefreshState = rememberPullRefreshState(refreshing = recargarGastos, onRefresh = {recargarGastos = !recargarGastos})
    var totalGastado by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(key1 = dineroRecaudado.value.isEmpty()) {
        Log.d("Donaciones", "Cargando donaciones")
        val donacionResponse = getDonationDataFromGoogleSheet(
            donacionesSheetId,
            "donaciones"
        )
        dineroRecaudado.value = donacionResponse.donaciones
    }
    LaunchedEffect(key1 = recargarGastos) {
        onMapaCambiado(true)
        gastoResponse = getGastosFromSheet()
        listaGastos = gastoResponse.gastos
        totalGastado = calculaTotalGastado(listaGastos)
        recargarGastos = false
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .pullRefresh(pullRefreshState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PullRefreshIndicator(
            refreshing = recargarGastos,
            state = pullRefreshState,
        )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(0.1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Gastos",
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.Center),
                    )
                    if (usuario.nombreRango != "Voluntario") {
                        IconButton(
                            onClick = { showGastoDialog = true },
                            modifier = Modifier
                                .size(65.dp)
                                .align(Alignment.CenterEnd)
                                .padding(end = 30.dp)
                        )
                        {
                            Icon(Icons.Filled.AddCircle, "Agregar sitio", Modifier.fillMaxSize())
                        }
                    }
                }
            }
        if (listaGastos.isNotEmpty() && !recargarGastos && dineroRecaudado.value.isNotEmpty()) {
            val donacionesTotales = dineroRecaudado.value[3].cantidad.split(" ")[0]
            val donacionesTotalesLimpio = donacionesTotales.replace(".", "")
            val dineroRecaudado = donacionesTotalesLimpio.replace(",", ".").toDoubleOrNull() ?: 0.0
            val dineroRestante = dineroRecaudado - totalGastado

            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .background(Color.Transparent),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                LazyColumn (
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(12.dp),
                    ) {
                    items(listaGastos.size) { index ->
                        GastoCard(gasto = listaGastos[index])
                    }
                }
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .weight(0.15f)
                        .clip(CircleShape)
                        .background(Color(semaforoDineroRestante(dineroRestante, dineroRecaudado)))
                        .border(2.dp, BordeIndvCards, CircleShape)
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row (
                        modifier = Modifier
                            .weight(0.45f)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Recaudado: $dineroRecaudado€",
                            fontSize = 17.sp,
                            modifier = Modifier
                                .weight(0.53f)
                                .padding(start = 8.dp),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Gastado: $totalGastado€",
                            fontSize = 17.sp,
                            modifier = Modifier
                                .weight(0.47f)
                                .padding(end = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row (
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val formatoDecimal = DecimalFormat("#.##")
                        val resultadoRedondeado = formatoDecimal.format(dineroRestante).replace(",", ".").toDouble()
                        Text(text = "Restante: $resultadoRedondeado€", fontSize = 24.sp)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.googlesheetslogo),
                    contentDescription = "GoogleSheetsLogo",
                    modifier = Modifier.size(60.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cargando gastos...",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
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
    if (showGastoDialog) {
        Dialog(onDismissRequest = { showGastoDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(425.dp)
                    .padding(35.dp)
                    .background(FondoApp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Column(
                    Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = motivoGasto,
                        onValueChange = { motivoGasto = it },
                        label = {
                            Text(
                                text = "Motivo del gasto",
                                color = Color.Black
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FondoIndvCards,
                            unfocusedContainerColor = FondoIndvCards,
                            focusedBorderColor = BordeIndvCards,
                            unfocusedBorderColor = BordeIndvCards
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cantidadGasto,
                        onValueChange = { cantidadGasto = it },
                        label = {
                            Text(
                                text = "Cantidad del gasto",
                                color = Color.Black
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FondoIndvCards,
                            unfocusedContainerColor = FondoIndvCards,
                            focusedBorderColor = BordeIndvCards,
                            unfocusedBorderColor = BordeIndvCards
                        ))
                    Spacer(modifier = Modifier.height(8.dp))
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Fecha del gasto",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = fechaFormateada,
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable { fechaDialogState.show() }
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pagadoPor,
                        onValueChange = { pagadoPor = it },
                        label = {
                            Text(
                                text = "Pagado por",
                                color = Color.Black
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FondoIndvCards,
                            unfocusedContainerColor = FondoIndvCards,
                            focusedBorderColor = BordeIndvCards,
                            unfocusedBorderColor = BordeIndvCards
                        ))
                    Row(
                        Modifier
                            .weight(0.2f)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            Modifier
                                .weight(0.5f)
                                .padding(start = 8.dp, end = 4.dp)
                                .background(Color.Transparent),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = { showGastoDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BordeIndvCards
                                )
                            ) {
                                Text(
                                    text = "Cancelar",
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Column(
                            Modifier
                                .weight(0.5f)
                                .padding(start = 4.dp, end = 8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
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
                                Toast
                                    .makeText(
                                        context,
                                        "Por favor, llena todos los campos",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show() } },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BordeIndvCards
                                )) {
                                Text(
                                    text = "Guardar",
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun semaforoDineroRestante(dineroRestante: Double, dineroRecaudado: Double): Long {
    return when {
        dineroRestante < 0.0 -> 0xFFffabab
        dineroRestante > 0.0 && dineroRestante < dineroRecaudado * 0.15 -> 0xFFffabab
        dineroRestante >= dineroRecaudado * 0.15 && dineroRestante < dineroRecaudado * 0.5 -> 0xFFfeffab
        else -> 0xFFb6ffab
    }
}

fun calculaTotalGastado(gastos: List<Gasto>): Double {
    var total = 0.0
    for (gasto in gastos) {
        val cantidad = gasto.cantidadGasto.split(" ")[0]
                .replace(".", "")
                .trim().toDoubleOrNull() ?: 0.0
        total += cantidad
    }
    return total
}

@Composable
fun GastoCard(gasto: Gasto){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(CircleShape)
            .height(60.dp)
            .border(1.dp, BordeIndvCards, CircleShape),
        colors = CardDefaults.cardColors(
            containerColor = FondoIndvCards
        )
    ) {
        Row (
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                Modifier
                    .weight(0.5f)
                    .padding(0.4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = gasto.motivoGasto, fontSize = 18.sp)
            }
            Column (
                Modifier.weight(0.3f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = gasto.fechaGasto, fontSize = 16.sp)
            }
            Column (
                Modifier.weight(0.2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = gasto.cantidadGasto, fontSize = 18.sp)
            }
        }
    }
}

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