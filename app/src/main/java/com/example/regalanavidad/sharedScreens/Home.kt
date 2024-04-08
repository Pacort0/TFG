package com.example.regalanavidad.sharedScreens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.organizadorScreens.OrganizadorHomeScreen
import com.example.regalanavidad.voluntarioScreens.VoluntarioHomeScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

val drawerItems = listOf("Información", "Contáctanos", "Patrocinadores", "Otros años")
val auth = Firebase.auth
var usuario = Usuario()
val firestore = FirestoreManager()

class Home : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        val correo = intent.getStringExtra("correo")

        runBlocking {
            val task = launch {
                usuario = correo?.let { firestore.findUserByEmail(it) }!!
            }
            task.join()
        }

        val esVoluntario = usuario.nombreRango == "Voluntario"

        super.onCreate(savedInstanceState)

        setContent {
            var estadoMapa by remember { mutableStateOf(false) }

            if(esVoluntario){
                VoluntarioHomeScreen(estadoMapa){
                    estado -> estadoMapa = estado
                }
            } else {
                OrganizadorHomeScreen(estadoMapa){
                    estado -> estadoMapa = estado
                }
            }
        }
    }
}

@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController, onTabSelected: (String) -> Unit) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        // looping over each tab to generate the views and navigation for each item
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    onTabSelected(tabBarItem.title) // Invoke the callback with the selected tab's title
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {Text(tabBarItem.title)})
        }
    }
}

// This component helps to clean up the API call from our TabView above,
// but could just as easily be added inside the TabView without creating this custom component
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
            contentDescription = title
        )
    }
}

// This component helps to clean up the API call from our TabBarIconView above,
// but could just as easily be added inside the TabBarIconView without creating this custom component
@Composable
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier, screenTitle: String, navController: NavController, mapaAbierto: Boolean, OnMapaCambiado: (Boolean) -> Unit) {
    when (screenTitle){
        "Home" -> {
            HomeScreen(modifier)
            OnMapaCambiado(false)
        }
        "Alerts" -> {
            AlertsScreen(modifier = modifier)
            OnMapaCambiado(false)
        }
        "Mapa" -> {
            MapsScreen(modifier = modifier, navController)
            OnMapaCambiado(true)
        }
        "More" -> {
            MoreTabsScreen(modifier = modifier)
            OnMapaCambiado(false)
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier){
    auth.currentUser?.reload() // Recargamos el usuario para comprobar cualquier actualización

    val context = LocalContext.current
    var agregaSitio by remember { mutableStateOf(false) }
    var muestraListaSitios by remember { mutableStateOf(false) }
    var textoBusqueda by remember { mutableStateOf("") }

    if (muestraListaSitios) {
        Dialog(onDismissRequest = { muestraListaSitios = false }) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(35.dp)
                .clip(RoundedCornerShape(20.dp))) {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(color = Color.White)) {
                    item {
                        Text(text = "Hola", color = Color.Black) }
                }
                FloatingActionButton(
                    onClick = {
                        agregaSitio = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(0.dp, 0.dp, 14.dp, 14.dp)) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar sitio")
                }
            }
        }
        if (agregaSitio) {
            Dialog(onDismissRequest = { agregaSitio = false }) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(35.dp)
                    .clip(RoundedCornerShape(20.dp))) {
                    OutlinedTextField(
                        value = textoBusqueda,
                        onValueChange = { newText ->
                            textoBusqueda = newText
                            // Llamamos a la API de Places o de Maps para cargar la lista de sugerencias
                        },
                        label = { Text("Buscar") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Hola ${usuario.nombre}!",
            modifier = modifier.padding(0.dp,10.dp)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(0.dp, 5.dp)) {
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(0.dp, 0.dp, 5.dp, 0.dp)
                    .let {
                        if (usuario.nombreRango == "Coordinador" || usuario.nombreRango == "Tesorería") {
                            it.clickable {
                                Toast
                                    .makeText(context, "Clickado", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else it
                    }) {
                    Column {
                        Text(text = "Dinero recaudado: 1€")
                    }
                }
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp, 0.dp, 0.dp, 0.dp)
                    .let {
                        if (usuario.nombreRango == "Coordinador" || usuario.nombreRango == "RR.II." || usuario.nombreRango == "Logística") {
                            it.clickable {
                                Toast
                                    .makeText(context, "Clickado", Toast.LENGTH_SHORT)
                                    .show()
                                muestraListaSitios = true
                            }
                        } else it
                    }) {
                    Column {
                        Text(text = "Sitios en los que recogemos: ")
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(5.dp, 0.dp)) {
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(0.dp, 0.dp, 5.dp, 0.dp)
                    .let {
                        if (usuario.nombreRango == "Coordinador" || usuario.nombreRango == "RR.II.") {
                            it.clickable {
                                Toast
                                    .makeText(context, "Clickado", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else it
                    }) {
                    Column {
                        Text(text = "Fechas y eventos")
                    }
                }
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp, 0.dp, 0.dp, 0.dp)
                    .let {
                        if (usuario.nombreRango == "Coordinador" || usuario.nombreRango == "Imagen") {
                            it.clickable {
                                Toast
                                    .makeText(context, "Clickado", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else it
                    }) {
                    Column {
                        Text(text = "Redes sociales: ")
                    }
                }
            }
        }
    }
}
@Composable
fun AlertsScreen(modifier: Modifier){
    Text(
        text = "Hello ${usuario.uid}!",
        modifier = modifier
    )
}

@Composable
fun MoreTabsScreen(modifier: Modifier){
    Text(
        text = "Hello ${usuario.nombreRango}!",
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionSubMenu(navController: NavController, drawerState: DrawerState, scope: CoroutineScope){
    val options = listOf("¿Qué es Regala Navidad?", "Datos y objetivos", "¿Cómo puedo ayudar?") // Add your sub-options here
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = "Información",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, fontSize = 16.sp) },
                    onClick = {
                        when(selectionOption){
                            "¿Qué es Regala Navidad?" -> {
                                selectedOptionText = selectionOption
                                navController.navigate("QueEsScreen")
                            }
                            "Datos y objetivos" -> {
                                selectedOptionText = selectionOption
                                navController.navigate("DatosYObjetivosScreen")
                            }
                            "¿Cómo puedo ayudar?" -> {
                                selectedOptionText = selectionOption
                                navController.navigate("ComoAyudarScreen")
                            }
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

@Composable
fun ShowDialog(showDialog: MutableState<Boolean>) {
    val context = LocalContext.current

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                //Cierra el mensaje de alerta cuando el usuario pincha fuera de la pantalla o en el botón de 'Atrás'
                showDialog.value = false
            },
            title = {
                Text(text = "¿Está seguro?")
            },
            text = {
                Text("¿Desea cerrar su sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        auth.signOut()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Sí, estoy seguro")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}

fun drawerAbierto(drawerValue: DrawerValue, mapaAbierto: Boolean): Boolean {
    return drawerValue == DrawerValue.Open || !mapaAbierto
}

/* Si hay tiempo retomamos esta idea (cambio foto perfil)
@Composable
fun SelectProfilePictureScreen(images: List<ImageBitmap>, onImageSelected: (Int) -> Unit) {
    LazyColumn {
        itemsIndexed(images) { index, image ->
            Image(
                bitmap = image,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .height(100.dp)
                    .clickable { onImageSelected(index) }
            )
        }
    }
}*/
