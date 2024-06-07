package com.example.regalanavidad.organizadorScreens

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.dal.getRecaudacionesFromSheet
import com.example.regalanavidad.dal.updateProdDataInGoogleSheet
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.DetallesProducto
import com.example.regalanavidad.modelos.Producto
import com.example.regalanavidad.modelos.ProductoResponse
import com.example.regalanavidad.sharedScreens.NoInternetScreen
import com.example.regalanavidad.sharedScreens.PantallaCarga
import com.example.regalanavidad.sharedScreens.hayInternet
import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.example.regalanavidad.ui.theme.FondoMenus
import com.example.regalanavidad.ui.theme.FondoTarjetaInception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PaginaSheetRecaudaciones(navController: NavController, onMapaCambiado: (Boolean) -> Unit){
    val spreadsheetId = "1KqoL4QJ6ER6f7gLy6k3fT0B4efCB2xNcHHxQzEe0mZ8"
    var listaProductos by remember { mutableStateOf(emptyList<Producto>()) }
    var productoResponse: ProductoResponse
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val opcionesDistritos = listOf("No Perecederos", "Conservas", "Desayuno", "Ingr. Básicos", "Higiene", "Prod. Bebés", "Prod. Navideños")
    var productoSeleccionado by remember { mutableStateOf(opcionesDistritos[0]) }
    var productosLoading by remember { mutableStateOf(true) }
    var listaProductosCambiados by remember {mutableStateOf(listOf<Producto>())}
    var showAlertDialog by remember { mutableStateOf(false)}
    var llamadaBackHandler by remember { mutableStateOf(false) }
    var opcionSeleccionada by remember {mutableStateOf("")}
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(refreshing = productosLoading, onRefresh = {productosLoading = !productosLoading})
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var hayInternet by remember { mutableStateOf(hayInternet(connectivityManager)) }
    var mostrarTodo by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = productosLoading) {
        onMapaCambiado(true)
        hayInternet = hayInternet(connectivityManager)
        if (hayInternet){
            productoResponse = getRecaudacionesFromSheet(spreadsheetId, cambiaNombreProducto(productoSeleccionado))
            listaProductos = productoResponse.productos
        }
        mostrarTodo = hayInternet
        productosLoading = false
    }

    if(productosLoading){
        PantallaCarga(textoCargando = "Cargando recaudaciones...")
    } else if (!mostrarTodo){
        NoInternetScreen(
            onRetry = {
                productosLoading = true
                listaProductosCambiados = emptyList<Producto>().toMutableList()
            }
        )
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .pullRefresh(pullRefreshState)) {
            Column (
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Productos Recaudados",
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row (
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier
                                .clip(CircleShape)
                                .border(0.dp, Color.Transparent, CircleShape)
                                .background(FondoTarjetaInception)
                        ) {
                            TextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .clip(CircleShape)
                                    .border(0.dp, Color.Transparent, CircleShape),
                                readOnly = true,
                                value = productoSeleccionado,
                                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 15.sp, color = Color.Black),
                                onValueChange = {},
                                trailingIcon = { TrailingIconMio(expanded = expanded) },
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = FondoTarjetaInception,
                                    focusedContainerColor = FondoTarjetaInception
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(FondoTarjetaInception)
                            ) {
                                opcionesDistritos.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption, fontSize = 18.sp, color = Color.Black) },
                                        onClick = {
                                            if (listaProductosCambiados.isNotEmpty()) {
                                                showAlertDialog = true
                                                opcionSeleccionada = selectionOption
                                            } else {
                                                productoSeleccionado = selectionOption
                                                expanded = false
                                                productosLoading = true
                                            }
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        modifier = Modifier
                                            .background(FondoTarjetaInception)
                                            .border(1.dp, ColorLogo, RoundedCornerShape(15.dp))
                                            .padding(5.dp),
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                    ) {
                        if (listaProductosCambiados.isNotEmpty()) {
                            ElevatedButton(onClick = {
                                if (hayInternet){
                                    Toast.makeText(context, "Actualizando productos...", Toast.LENGTH_SHORT).show()
                                    scope.launch(Dispatchers.IO) {
                                        updateProdDataInGoogleSheet(
                                            spreadsheetId,
                                            cambiaNombreProducto(productoSeleccionado),
                                            listaProductosCambiados
                                        )
                                        listaProductosCambiados = emptyList()
                                        productosLoading = true
                                    }
                                } else {
                                    Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
                                }
                            }, modifier = Modifier.width(160.dp),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = FondoMenus
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.save),
                                        contentDescription = "Guardar cambios",
                                        Modifier.size(32.dp),
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Guardar", fontSize = 18.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                }
                if (listaProductos.isNotEmpty() && !productosLoading){
                    LazyColumn {
                        items(listaProductos.size) { index ->
                            var isExpanded by remember { mutableStateOf(false) }  // Añadir estado para controlar la expansión
                            Card (
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillParentMaxWidth()
                                    .heightIn(min = 45.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(0.dp, Color.Transparent, RectangleShape)
                                    .animateContentSize(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            easing = LinearOutSlowInEasing
                                        )
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = FondoIndvCards
                                )
                            ) {
                                Row (
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { isExpanded = !isExpanded }
                                        .padding(8.dp)){
                                    Column(
                                        Modifier
                                            .weight(0.1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center)
                                    {
                                        Icon(imageVector = isExpanded.let { if (!isExpanded) {
                                            Icons.Default.KeyboardArrowDown
                                        } else {
                                            Icons.Default.KeyboardArrowUp
                                        } }, contentDescription = "Contraer", Modifier.size(30.dp), tint = Color.Black)
                                    }
                                    Column (Modifier.weight(0.45f), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                                        Text(
                                            text = listaProductos[index].nombre,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black)
                                    }
                                    Column (Modifier.weight(0.45f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                                        Text(
                                            text = "Cantidad total: ${listaProductos[index].cantidadTotal}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black)
                                    }
                                }
                                if (isExpanded) {
                                    Column {
                                        listaProductos[index].tipos.forEach { tipo ->
                                            val cantidadOriginalProd by remember { mutableIntStateOf(tipo.cantidad.toInt()) }
                                            var cantidadProd by remember { mutableIntStateOf(tipo.cantidad.toInt()) }
                                            Row (
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .border(0.dp, Color.Transparent, CircleShape)
                                                    .clip(CircleShape)
                                                    .background(FondoTarjetaInception)
                                                    .padding(8.dp)){
                                                Column (Modifier.weight(0.35f),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center)
                                                {
                                                    Text(
                                                        text = tipo.tipo,
                                                        fontSize = 20.sp,
                                                        color = Color.Black)
                                                }
                                                Column (Modifier
                                                    .weight(0.65f),
                                                    horizontalAlignment = Alignment.End,
                                                    verticalArrangement = Arrangement.Center)
                                                {
                                                    Row (
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(end = 12.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.End
                                                    ) {
                                                        IconButton(onClick = {
                                                            cantidadProd--
                                                            tipo.cantidad = cantidadProd.toString()

                                                            val producto = listaProductos[index]
                                                            val productoExistente = listaProductosCambiados.find { it.nombre == producto.nombre }

                                                            listaProductosCambiados = gestionaLista(
                                                                cantidadProd,
                                                                cantidadOriginalProd,
                                                                productoExistente,
                                                                listaProductosCambiados,
                                                                listaProductos,
                                                                index,
                                                                tipo.tipo
                                                            )
                                                        }) {
                                                            Icon(painterResource(id = R.drawable.menos), contentDescription = "Quitar", Modifier.size(31.dp), tint = Color.Black)
                                                        }
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        TextField(
                                                            value = "$cantidadProd",
                                                            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 20.sp, color = Color.Black),
                                                            onValueChange = { cantidad ->
                                                                val trimmedCantidad = cantidad.trim() // Eliminar espacios en Color.Black
                                                                if (trimmedCantidad.isEmpty()) {
                                                                    cantidadProd = 0 // O cualquier valor predeterminado
                                                                } else {
                                                                    val nuevaCantidad = trimmedCantidad.toInt()
                                                                    cantidadProd = nuevaCantidad
                                                                }
                                                                tipo.cantidad = cantidadProd.toString()

                                                                val producto = listaProductos[index]
                                                                val productoExistente = listaProductosCambiados.find { it.nombre == producto.nombre }

                                                                listaProductosCambiados = gestionaLista(
                                                                    cantidadProd,
                                                                    cantidadOriginalProd,
                                                                    productoExistente,
                                                                    listaProductosCambiados,
                                                                    listaProductos,
                                                                    index,
                                                                    tipo.tipo
                                                                )
                                                            },
                                                            colors = TextFieldDefaults.colors(
                                                                unfocusedIndicatorColor = Color.Transparent,
                                                                focusedIndicatorColor = Color.Transparent,
                                                                focusedContainerColor = Color.Transparent,
                                                                unfocusedContainerColor = Color.Transparent,
                                                            ),
                                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                            modifier = Modifier
                                                                .width(70.dp)
                                                                .wrapContentHeight()
                                                                .background(Color.Transparent)
                                                                .align(Alignment.CenterVertically),
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        IconButton(onClick = {
                                                            cantidadProd++
                                                            tipo.cantidad = cantidadProd.toString()

                                                            val producto = listaProductos[index]
                                                            val productoExistente = listaProductosCambiados.find { it.nombre == producto.nombre }

                                                            listaProductosCambiados = gestionaLista(
                                                                cantidadProd,
                                                                cantidadOriginalProd,
                                                                productoExistente,
                                                                listaProductosCambiados,
                                                                listaProductos,
                                                                index,
                                                                tipo.tipo
                                                            )
                                                        }) {
                                                            Icon(Icons.Filled.AddCircle, contentDescription = "Añadir", Modifier.size(31.dp), tint = Color.Black)
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
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
                        Image(
                            painter = painterResource(id = R.drawable.googlesheetslogo),
                            contentDescription = "GoogleSheetsLogo",
                            modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cargando productos...",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = productosLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
    if(showAlertDialog){
        AlertDialog(
            containerColor = FondoApp,
            onDismissRequest = {
                showAlertDialog = false
            },
            title = {
                Text(text = "Tiene cambios sin guardar", color = Color.Black)
            },
            text = {
                Text("Si sale de la página sin guardar los cambios, perderá la información modificada.\n¿Está seguro de querer continuar?",
                    color = Color.Black)
            },
            confirmButton = {
                if(llamadaBackHandler){
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FondoTarjetaInception
                        ),
                        onClick = {
                            showAlertDialog = false
                            navController.popBackStack()
                        }
                    ){
                        Text("Sí, estoy seguro", color = Color.Black)
                    }
                } else {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FondoTarjetaInception
                        ),
                        onClick = {
                            showAlertDialog = false
                            productoSeleccionado = opcionSeleccionada
                            expanded = false
                            productosLoading = true
                            listaProductosCambiados = emptyList<Producto>().toMutableList()
                        }
                    ) {
                        Text("Sí, estoy seguro", color = Color.Black)
                    }
                }
            },
            dismissButton = {
                if (llamadaBackHandler){
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FondoTarjetaInception
                        ),
                        onClick = {
                            showAlertDialog = false
                            llamadaBackHandler = false
                        }
                    ){
                        Text(text = "No", color = Color.Black)
                    }
                } else {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FondoTarjetaInception
                        ),
                        onClick = {
                            showAlertDialog = false
                        }
                    ) {
                        Text("No", color = Color.Black)
                    }
                }
            }
        )
    }
    BackHandler {
        if(listaProductosCambiados.isNotEmpty()){
            llamadaBackHandler = true
            showAlertDialog = true
        } else {
            navController.popBackStack()
            listaProductosCambiados = emptyList<Producto>().toMutableList()
        }
    }
}

private fun gestionaLista(
    cantidadProd: Int,
    cantidadOriginal: Int,
    productoExistente: Producto?,
    listaProductosCambiados: List<Producto>,
    listaProductos: List<Producto>,
    index: Int,
    tipo: String
): MutableList<Producto> {
    val listaCambiada = listaProductosCambiados.toMutableList()

    if (cantidadProd != cantidadOriginal) {
        if (productoExistente == null) {
            // Si el producto no está en la lista, agregar solo el tipo alterado
            val nuevoProducto = Producto(
                nombre = listaProductos[index].nombre,
                tipos = listOf(DetallesProducto(tipo = tipo, cantidad = cantidadProd.toString())),
                cantidadTotal = "" // Esto se puede ajustar según sea necesario
            )
            listaCambiada.add(nuevoProducto)
        } else {
            // Si el producto ya está en la lista, actualizar solo el tipo específico
            val tipoExistente = productoExistente.tipos.find { it.tipo == tipo }
            if (tipoExistente != null) {
                tipoExistente.cantidad = cantidadProd.toString()
            } else {
                // Agregar el nuevo tipo al producto existente
                productoExistente.tipos += DetallesProducto(tipo = tipo, cantidad = cantidadProd.toString())
            }
        }
    } else {
        // Si el producto está en la lista y su cantidad es igual a la original, eliminar el tipo específico
        if (productoExistente != null) {
            val tipoExistente = productoExistente.tipos.find { it.tipo == tipo }
            if (tipoExistente != null) {
                val nuevosTipos = productoExistente.tipos.filter { it.tipo != tipo }
                if (nuevosTipos.isEmpty()) {
                    // Si no quedan tipos, eliminar el producto
                    listaCambiada.remove(productoExistente)
                } else {
                    // Si quedan otros tipos, actualizar el producto con los tipos restantes
                    val productoActualizado = productoExistente.copy(tipos = nuevosTipos)
                    listaCambiada[listaCambiada.indexOf(productoExistente)] = productoActualizado
                }
            }
        }
    }

    return listaCambiada
}

fun cambiaNombreProducto(sheetName: String):String {
    var nuevoSheetName = ""
    when (sheetName) {
        "No Perecederos" -> {
            nuevoSheetName = "ProductosNP"
        }

        "Ingr. Básicos" -> {
            nuevoSheetName = "IngrBasicos"
        }

        "Prod. Bebés" -> {
            nuevoSheetName = "ProdBebes"
        }

        "Prod. Navideños" -> {
            nuevoSheetName = "ProdNav"
        }
    }
    return if (nuevoSheetName != "") {
        nuevoSheetName
    } else {
        sheetName
    }
}