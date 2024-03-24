package com.example.regalanavidad.voluntarioScreens

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.regalanavidad.MainActivity
import com.example.regalanavidad.ProfileScreen
import com.example.regalanavidad.ScreenContent
import com.example.regalanavidad.TabBarItem
import com.example.regalanavidad.TabView
import com.example.regalanavidad.auth
import com.example.regalanavidad.drawerItems
import com.example.regalanavidad.ui.theme.RegalaNavidadTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoluntarioHomeScreen(){
    var currentTabTitle by remember { mutableStateOf("Home") }

    // setting up the individual tabs
    val homeTab = TabBarItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    val mapsTab = TabBarItem(
        title = "Mapa",
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn
    )
    val moreTab = TabBarItem(
        title = "More",
        selectedIcon = Icons.AutoMirrored.Filled.List,
        unselectedIcon = Icons.AutoMirrored.Outlined.List
    )
    val tabBarItems = listOf(homeTab, mapsTab, moreTab)
    val navController = rememberNavController()
    RegalaNavidadTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalItems(
                        navController = navController,
                        scope = scope,
                        drawerState = drawerState,
                    )
                },
                content = {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(text = currentTabTitle, fontSize = 25.sp) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { navController.navigate("profileScreen") }) {
                                        Icon(
                                            Icons.Filled.AccountCircle,
                                            "Localized description",
                                        )
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            TabView(tabBarItems, navController) { title ->
                                currentTabTitle = title // Update the current tab's title
                            }
                        },
                        content = { innerPadding -> //NavHost
                            VoluntarioNavHost(innerPadding, navController, homeTab, mapsTab, moreTab)
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun VoluntarioNavHost(innerPadding : PaddingValues, navController: NavHostController, homeTab:TabBarItem, mapsTab:TabBarItem, moreTab:TabBarItem){
    Box(modifier = Modifier.padding(innerPadding)) {
        NavHost(
            navController = navController,
            startDestination = homeTab.title
        ) {
            composable(homeTab.title) {
                ScreenContent(screenTitle = homeTab.title)
            }
            composable(mapsTab.title) {
                ScreenContent(screenTitle = mapsTab.title)
            }
            composable(moreTab.title) {
                ScreenContent(screenTitle = moreTab.title)
            }
            composable("profileScreen"){
                ProfileScreen(navController = navController)
            }
            composable("QueEsScreen"){
                QueEs()
            }
            composable("ComoAyudarScreen"){
                ComoAyudar()
            }
            composable("DatosYObjetivosScreen"){
                DatosYObjetivos()
            }
            composable("ContactanosScreen"){
                ContactanosScreen()
            }
            composable("PatrocinadoresScreen"){
                PatrocinadoresScreen()
            }
            composable("OtrosAnosScreen"){
                OtrosAnosScreen()
            }
        }
    }
}

@Composable
fun ModalItems(navController: NavController, scope: CoroutineScope, drawerState: DrawerState){
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        ShowDialog(showDialog = showDialog)
    }
    ModalDrawerSheet (modifier = Modifier.width(270.dp)) {
        Text("Regala Navidad", modifier = Modifier.padding(16.dp))
        HorizontalDivider()
        drawerItems.forEach { item ->
            if (item == "Información") {
               InformacionSubMenu(navController = navController, drawerState = drawerState, scope = scope)
            } else {
                NavigationDrawerItem(
                    label = { Text(text = item) },
                    selected = false, // Set the selected state as needed
                    onClick = {
                        when (item) {
                            "Contáctanos" -> {
                                navController.navigate("ContactanosScreen")
                            }
                            "Patrocinadores" -> {
                                navController.navigate("PatrocinadoresScreen")
                            }
                            "Otros años" -> {
                                navController.navigate("OtrosAnosScreen")
                            }
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f)) // This will push the 'Sign out' option to the bottom
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text(text = "Cerrar sesión") },
            selected = false,
            onClick = {
                showDialog.value = true
                scope.launch { drawerState.close() }
            }
        )
        Spacer(modifier = Modifier.weight(0.1f))
    }
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
