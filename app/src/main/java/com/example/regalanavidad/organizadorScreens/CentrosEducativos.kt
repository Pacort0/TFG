package com.example.regalanavidad.organizadorScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaginaSheetCentrosEducativos(navController: NavController) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var expanded by remember { mutableStateOf(false) }
    val opcionesDistritos = listOf("Sevilla Este", "Aljarafe", "Montequinto", "Casco Antiguo", "Nervión-Porvenir", "Triana", "Heliópolis", "Facultades US")
    var distritoSeleccionado by remember { mutableStateOf(opcionesDistritos[3]) }
    var centrosLoading by remember { mutableStateOf(true) }

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
    }
}