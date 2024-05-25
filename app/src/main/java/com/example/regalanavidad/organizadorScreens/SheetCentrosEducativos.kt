package com.example.regalanavidad.organizadorScreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.navigation.NavController
import com.example.regalanavidad.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaginaSheetCentrosEducativos(navController: NavController) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var expanded by remember { mutableStateOf(false) }
    val opcionesDistritos = listOf("Sevilla Este", "Aljarafe", "Montequinto", "Casco Antiguo", "Nervión-Porvenir", "Triana", "Heliópolis", "Facultades US")
    var distritoSeleccionado by remember { mutableStateOf(opcionesDistritos[3]) }
    var centrosLoading by remember { mutableStateOf(true) }
    var showAlertDialog by remember { mutableStateOf(false)}
    var opcionSeleccionada by remember {mutableStateOf("")}
    var llamadaBackHandler by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = centrosLoading) {
        informacionCentrosRecogida.value = getCentrosFromDistrito(distritoSeleccionado = distritoSeleccionado)
        centrosLoading = false
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
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
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Cargando centros...",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = {
                val distrito: String
                if (listaEstadosCentrosCambiados.value.isNotEmpty()){
                    distrito = cambiaNombreDistrito(distritoSeleccionado)
                    scope.launch(Dispatchers.IO) {
                        updateCentrosDataInGoogleSheet(infoCentrosSheetId, distrito, listaEstadosCentrosCambiados.value)
                    }
                    listaEstadosCentrosCambiados.value = emptyList()
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
                } else {
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
        }
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