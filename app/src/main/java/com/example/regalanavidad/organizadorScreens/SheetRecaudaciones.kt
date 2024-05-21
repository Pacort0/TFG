package com.example.regalanavidad.organizadorScreens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.Producto
import com.example.regalanavidad.modelos.ProductoResponse
import com.example.regalanavidad.modelos.RequestPostRecaudacion
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaginaSheetRecaudaciones(){
    val spreadsheetId = "1KqoL4QJ6ER6f7gLy6k3fT0B4efCB2xNcHHxQzEe0mZ8"
    var listaProductos by remember { mutableStateOf(emptyList<Producto>()) }
    var recargarRecaudaciones by remember { mutableStateOf(true) }
    var productoResponse: ProductoResponse
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var expanded by remember { mutableStateOf(false) }
    val opcionesDistritos = listOf("No Perecederos", "Conservas", "Desayuno", "IngrBasicos", "Higiene", "Triana", "ProdBebes", "ProdNav")
    var productoSeleccionado by remember { mutableStateOf(opcionesDistritos[1]) }
    var productosLoading by remember { mutableStateOf(false) }
    val listaProductosCambiados by remember { mutableStateOf(emptyList<Producto>()) }

    LaunchedEffect(key1 = productosLoading) {
        productoResponse = getRecaudacionesFromSheet(spreadsheetId, productoSeleccionado)
        listaProductos = productoResponse.productos
        recargarRecaudaciones = false
        productosLoading = false
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        Column {
            Text(
                text = "Selecciona el tipo de producto",
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
                    value = productoSeleccionado,
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
                                productoSeleccionado = selectionOption
                                expanded = false
                                productosLoading = true
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            if (listaProductos.isNotEmpty() && !productosLoading){
                LazyColumn {
                    items(listaProductos.size) { index ->
                        Card (
                            modifier = Modifier
                                .padding(8.dp)
                                .fillParentMaxWidth()
                                .height(60.dp)
                        ) {
                            Row (
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize().padding(8.dp)){
                                Column (Modifier.weight(0.55f), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                                    Text(text = listaProductos[index].nombre)
                                }
                                Column (Modifier.weight(0.45f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                                    Text(text = "Cantidad total: ${listaProductos[index].cantidadTotal}")
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
                    CircularProgressIndicator()
                    Text(
                        text = "Cargando productos...",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        if (listaProductosCambiados.isNotEmpty()){
            FloatingActionButton(
                onClick = {
                    val producto: String
                    if (listaProductosCambiados.isNotEmpty()){
                        producto = cambiaNombreSheet(productoSeleccionado)
                        scope.launch(Dispatchers.IO) {
                            updateProdDataInGoogleSheet(infoCentrosSheetId, producto, listaProductosCambiados)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(0.dp, 0.dp, 14.dp, 14.dp)
                    .height(45.dp)
                    .width(180.dp)){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(painterResource(id = R.drawable.save), contentDescription = "Guardar cambios", Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Guardar cambios")
                }
            }
        }
    }
}

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
        val url = "https://script.google.com/macros/s/AKfycbygcfd8kcWN8C0fJw3Eh4vW15BhQ1GVu6cHw1MjO9rbe5bWgxxIjhk12SVGWenap40FPA/exec"
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