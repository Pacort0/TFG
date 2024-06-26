package com.example.regalanavidad.organizadorScreens

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.modelos.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.regalanavidad.R
import com.example.regalanavidad.sharedScreens.NoInternetScreen
import com.example.regalanavidad.sharedScreens.PantallaCarga
import com.example.regalanavidad.sharedScreens.firestore
import com.example.regalanavidad.sharedScreens.hayInternet
import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.example.regalanavidad.ui.theme.FondoTarjetaInception

private var listaUsuariosCambiados = mutableStateOf(emptyList<Usuario>())
private var listaUsuarios = mutableStateOf(emptyList<Usuario>())

//La estructura de esta página es muy similar a la de la página de Tareas, por lo que no hace falta comentar
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RolesTabScreen(navController: NavController){
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Voluntarios", "Roles")
    var showAlertDialog by remember { mutableStateOf(false) }
    var llamadaBackHandler by remember { mutableStateOf(false) }
    var indexActual by remember { mutableIntStateOf(0) }

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = FondoIndvCards,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]) ,
                        color = ColorLogo
                    )
                }
            }
        )
        {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selectedContentColor = Color.Black,
                    selected = selectedTabIndex == index,
                    onClick = {
                        if (listaUsuariosCambiados.value.isNotEmpty()) {
                            showAlertDialog = true
                            indexActual = index
                        } else {
                            selectedTabIndex = index
                        }
                    },
                    text = { Text(text = title, fontSize = 16.sp, color = Color.Black)}
                )
            }
        }
        // Renderizar el contenido basado en la pestaña seleccionada
        when (selectedTabIndex) {
            0 -> TabRoles(true)
            1 -> TabRoles(false)
        }

        // Mostrar el AlertDialog si es necesario
        if (showAlertDialog) {
            AlertDialog(
                containerColor = FondoApp,
                onDismissRequest = { showAlertDialog = false },
                title = { Text(text = "Tiene cambios sin guardar", color = Color.Black) },
                text = { Text("Perderá la información modificada.\n¿Está seguro de querer continuar?", color = Color.Black) },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorLogo
                        ),
                        onClick = {
                            showAlertDialog = false
                            listaUsuariosCambiados.value = emptyList()
                            listaUsuarios.value = emptyList()
                            if (llamadaBackHandler) {
                                navController.popBackStack()
                            } else {
                                selectedTabIndex = indexActual
                            }
                        }
                    ) {
                        Text("Continuar", color = Color.Black)
                    }
                },
                dismissButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorLogo
                        ),
                        onClick = {
                            showAlertDialog = false
                            llamadaBackHandler = false
                        }
                    ) {
                        Text("No", color = Color.Black)
                    }
                }
            )
        }
        // Manejo del botón de retroceso
        BackHandler {
            if (listaUsuariosCambiados.value.isNotEmpty()) {
                llamadaBackHandler = true
                showAlertDialog = true
            } else {
                navController.popBackStack()
                listaUsuariosCambiados.value = emptyList()
            }
        }
    }
    // Observa los cambios de navegación
    ObserveNavigationChanges(navController) {
        listaUsuariosCambiados.value = emptyList()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TabRoles(voluntarios: Boolean){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var guardarCambios by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var hayInternet by remember { mutableStateOf(hayInternet(connectivityManager)) }
    var mostrarTodo by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = guardarCambios) {
        hayInternet = hayInternet(connectivityManager)
        if (hayInternet) {
            Log.d("Roles", listaUsuariosCambiados.value.toString())
            listaUsuariosCambiados.value.forEach { usuario ->
                firestore.editaUsuario(usuario)
            }
            listaUsuariosCambiados.value = emptyList()
        } else {
            listaUsuariosCambiados.value = emptyList()
            Toast.makeText(context, "No hay internet", Toast.LENGTH_SHORT).show()
        }
        guardarCambios = false
    }

    LaunchedEffect(key1 = hayInternet, key2 = Unit) {
        cargando = true
        hayInternet = hayInternet(connectivityManager)
        if (hayInternet){
            val usuarios = firestore.getUsuarios()
            listaUsuarios.value = usuarios
        }
        cargando = false
        mostrarTodo = hayInternet
    }

    if (cargando || guardarCambios){
        PantallaCarga("Cargando usuarios...")
    } else if (!mostrarTodo){
        NoInternetScreen(
            onRetry = {
                hayInternet = true
            }
        )
    } else {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp)
                .padding(10.dp)
        ) {
            Column (
                Modifier.fillMaxSize()
            ) {
                Text("Asignar roles",
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(8.dp), fontSize = 24.sp, textAlign = TextAlign.Center, color = Color.Black)
                if (!guardarCambios && listaUsuarios.value.isEmpty()){
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = ColorLogo
                        )
                        Text(
                            text = "Cargando usuarios...",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                } else {
                    val usuariosFiltrados by remember { mutableStateOf(listaUsuarios.value.filter { usuario ->
                        if (voluntarios) {
                            usuario.nombreRango == "Voluntario"
                        } else {
                            usuario.nombreRango != "Voluntario"
                        }
                    }) }
                    LazyColumn (
                        Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(8.dp)) {
                        if (usuariosFiltrados.isEmpty()){
                            item {
                                Text("Todos los usuarios son Voluntarios", Modifier.padding(8.dp))
                            }
                        } else {
                            items(usuariosFiltrados) { usuarioRegistrado ->
                                if (usuarioRegistrado.nombreRango != "Coordinador"){
                                    Card (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .border(0.dp, Color.Transparent, RoundedCornerShape(15.dp))
                                            .wrapContentHeight(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = FondoIndvCards
                                        )) {
                                        Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                                            Column (Modifier.weight(0.4f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                                Text(text = usuarioRegistrado.nombre, fontSize = 18.sp, color = Color.Black)
                                            }
                                            Column (Modifier.weight(0.6f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                                                RolesSubMenu(drawerState, scope, usuarioRegistrado)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
            if(listaUsuariosCambiados.value.isNotEmpty()){
                FloatingActionButton(
                    containerColor = FondoTarjetaInception,
                    onClick = {
                        guardarCambios = true
                        Toast.makeText(context, "Actualizando roles...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp, 0.dp, 0.dp, 30.dp)
                        .height(40.dp)
                        .width(130.dp)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.save),
                            contentDescription = "Guardar",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Black)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Guardar",
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolesSubMenu(drawerState: DrawerState, scope: CoroutineScope, usuarioRegistrado: Usuario){
    val options = listOf("Tesorería", "RR.II.", "Logística", "Imagen", "Voluntario")
    var expanded by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(usuarioRegistrado.nombreRango) }
    val rolOriginal by remember { mutableStateOf(usuarioRegistrado.nombreRango) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .border(0.dp, Color.Transparent, RoundedCornerShape(15.dp))
            .background(FondoTarjetaInception)
    ) {
        TextField(
            leadingIcon = {
                Icon(
                    painter = painterResource(id = asignaLogoSegunRol(rolSeleccionado)),
                    contentDescription = "Icono rol", Modifier.size(35.dp),
                    tint = Color.Black
                )
            },
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            value = rolSeleccionado,
            onValueChange = {},
            trailingIcon = { TrailingIconMio(expanded = expanded) },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = FondoTarjetaInception,
                unfocusedContainerColor = FondoTarjetaInception
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(FondoTarjetaInception)
                .clip(RoundedCornerShape(20.dp))
                .border(0.dp, Color.Transparent, RoundedCornerShape(15.dp))
        ) {
            options.filter { usuarioRegistrado.nombreRango != it }.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, fontSize = 18.sp, color = Color.Black) },
                    onClick = {
                        expanded = false
                        scope.launch { drawerState.close() }
                        if(selectionOption != rolOriginal){
                            if (!listaUsuariosCambiados.value.contains(usuarioRegistrado)){
                                usuarioRegistrado.nombreRango = selectionOption
                                listaUsuariosCambiados.value += usuarioRegistrado
                            } else {
                                listaUsuariosCambiados.value =
                                    listaUsuariosCambiados.value.map { usuario ->
                                        if (usuario == usuarioRegistrado) {
                                            usuario.nombreRango = selectionOption
                                        }
                                        usuario
                                    }
                            }
                        }
                        else {
                            if (listaUsuariosCambiados.value.contains(usuarioRegistrado)){
                                listaUsuariosCambiados.value -= usuarioRegistrado
                                usuarioRegistrado.nombreRango = rolOriginal
                            }
                        }
                        rolSeleccionado = selectionOption
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = asignaLogoSegunRol(selectionOption)),
                            contentDescription = "Icono rol", Modifier.size(32.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(3.dp)
                        .border(1.dp, ColorLogo, RoundedCornerShape(10.dp))
                        .wrapContentSize()
                )
            }
        }
    }
}

fun asignaLogoSegunRol(rol: String): Int {
    return when (rol) {
        "Tesorería" -> R.drawable.tesorero_icono
        "RR.II." -> R.drawable.rrii_icono
        "Logística" -> R.drawable.logistica_icono
        "Imagen" -> R.drawable.imagen_icono
        else -> R.drawable.voluntario_icono
    }
}