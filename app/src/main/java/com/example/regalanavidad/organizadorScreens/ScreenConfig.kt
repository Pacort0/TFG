package com.example.regalanavidad.organizadorScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.TabBarItem
import com.example.regalanavidad.sharedScreens.ProfileScreen
import com.example.regalanavidad.sharedScreens.ScreenContent
import com.example.regalanavidad.sharedScreens.CierraSesionDialog
import com.example.regalanavidad.sharedScreens.TabView
import com.example.regalanavidad.sharedScreens.checkIfCanManageEmails
import com.example.regalanavidad.sharedScreens.drawerAbierto
import com.example.regalanavidad.sharedScreens.usuario

import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.RegalaNavidadTheme
import com.example.regalanavidad.viewmodels.MapaVM
import com.example.regalanavidad.sharedScreens.ComoAyudar
import com.example.regalanavidad.sharedScreens.ContactanosScreen
import com.example.regalanavidad.sharedScreens.DatosYObjetivos
import com.example.regalanavidad.sharedScreens.OtrosAnosScreen
import com.example.regalanavidad.sharedScreens.PaginaSheetGastos
import com.example.regalanavidad.sharedScreens.PatrocinadoresScreen
import com.example.regalanavidad.sharedScreens.QueEs
import com.example.regalanavidad.ui.theme.FondoOpcionesSubMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val drawerItems = listOf("Información", "Contáctanos", "Patrocinadores", "Otros años")

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaPantallas(mapaAbierto: Boolean, mapaOrganizadorVM: MapaVM, onMapaCambiado: (Boolean) -> Unit) {
    var currentTabTitle by remember { mutableStateOf("Home") }
    val homeTab = TabBarItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    val tareasTab = TabBarItem(
        title = "Tareas",
        selectedIcon = ImageVector.vectorResource(id = R.drawable.lista_filled),
        unselectedIcon =  ImageVector.vectorResource(id = R.drawable.lista_outlined)
    )
    val mapsTab = TabBarItem(
        title = "Mapa",
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn
    )
    val rolesTab = TabBarItem(
        title = "Roles",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.Star
    )
    val sheetsTab = TabBarItem(
        title = "Excel",
        selectedIcon = ImageVector.vectorResource(id = R.drawable.sheets_filled),
        unselectedIcon =  ImageVector.vectorResource(id = R.drawable.sheets_outlined)
    )
    var tabBarItems = listOf(homeTab, tareasTab, mapsTab, sheetsTab)
    if (usuario.nombreRango == "Coordinador"){
        tabBarItems = listOf(homeTab, tareasTab, mapsTab, rolesTab, sheetsTab)
    }
    if (usuario.nombreRango == "Voluntario") {
        tabBarItems = listOf(homeTab, mapsTab)
    }
    val navController = rememberNavController()
    RegalaNavidadTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            // Observe navigation changes and update the current tab title and selected item
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route ?: homeTab.title

            currentTabTitle = tabBarItems.find { it.title == currentRoute }?.title ?: homeTab.title

            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = drawerAbierto(drawerState.currentValue, mapaAbierto),
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
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = ColorLogo
                                ),
                                title = { Text(text = currentTabTitle, fontSize = 25.sp, color = Color.Black) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                        mapaOrganizadorVM.searchSitioRecogida.value = false
                                    }) {
                                        Icon(Icons.Filled.Menu, contentDescription = "Localized description", tint = Color.Black )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        navController.navigate("profileScreen")
                                        mapaOrganizadorVM.searchSitioRecogida.value = false
                                    }) {
                                        Icon(
                                            Icons.Filled.AccountCircle,
                                            "Localized description",
                                            Modifier,
                                            Color.Black
                                        )
                                    }
                                }
                            )
                        },
                        content = { innerPadding -> //NavHost
                            OrganizadorNavHost(innerPadding, navController, homeTab, tareasTab, mapsTab, rolesTab, sheetsTab, onMapaCambiado, mapaOrganizadorVM)
                        },
                        bottomBar = {
                            TabView(tabBarItems, navController, mapaOrganizadorVM) { title ->
                                currentTabTitle = title
                            }
                        },
                    )
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OrganizadorNavHost(innerPadding : PaddingValues, navController: NavHostController, homeTab: TabBarItem, alertsTab: TabBarItem, mapsTab: TabBarItem, rolesTab: TabBarItem, sheetsTab: TabBarItem, onMapaCambiado: (Boolean) -> Unit, mapaOrganizadorVM: MapaVM){
    Box(modifier = Modifier.padding(innerPadding)) {
        NavHost(
            navController = navController,
            startDestination = homeTab.title
        ) {
            composable(homeTab.title) {
                ScreenContent(screenTitle = homeTab.title, navController = navController, onMapaCambiado = onMapaCambiado, mapaOrganizadorVM = mapaOrganizadorVM)
            }
            composable(alertsTab.title) {
                ScreenContent(screenTitle = alertsTab.title, navController = navController, onMapaCambiado = onMapaCambiado, mapaOrganizadorVM = mapaOrganizadorVM)
            }
            composable(mapsTab.title) {
                ScreenContent(screenTitle = mapsTab.title, navController = navController, onMapaCambiado = onMapaCambiado, mapaOrganizadorVM = mapaOrganizadorVM)
            }
            if (usuario.nombreRango == "Coordinador"){
                composable(rolesTab.title) {
                    ScreenContent(screenTitle = rolesTab.title, navController = navController, onMapaCambiado = onMapaCambiado, mapaOrganizadorVM = mapaOrganizadorVM)
                }
            }
            if(checkIfCanManageEmails(usuario.nombreRango)){
                composable("Mail"){
                    ScreenContent(screenTitle = "Mail", navController = navController, onMapaCambiado = onMapaCambiado, mapaOrganizadorVM = mapaOrganizadorVM)
                }
            }
            composable(sheetsTab.title) {
                ScreenContent(screenTitle = sheetsTab.title, navController = navController, onMapaCambiado = onMapaCambiado, mapaOrganizadorVM = mapaOrganizadorVM)
            }
            composable("SheetCentrosEducativos"){
                PaginaSheetCentrosEducativos(navController, onMapaCambiado)
            }
            composable("SheetGastos"){
                PaginaSheetGastos(onMapaCambiado)
            }
            composable("SheetRecaudaciones"){
                PaginaSheetRecaudaciones(navController, onMapaCambiado)
            }
            composable("profileScreen"){
                ProfileScreen()
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
    val showCerrarSesionDialog = remember { mutableStateOf(false) }

    if (showCerrarSesionDialog.value) {
        CierraSesionDialog(showDialog = showCerrarSesionDialog, navController)
    }
    ModalDrawerSheet(
        modifier = Modifier.width(270.dp),
        drawerContainerColor = FondoApp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.regala_navidad_fondo),
                contentDescription = "FondoRN",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        HorizontalDivider()
        drawerItems.forEach { item ->
            if (item == "Información") {
                InformacionSubMenu(navController = navController, drawerState = drawerState, scope = scope)
            } else {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = asignaLogoSegunOpcion(item)),
                            contentDescription = "Icono opción",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = FondoApp,
                        unselectedContainerColor = FondoApp,
                        selectedIconColor = Color.Black,
                        unselectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        unselectedTextColor = Color.Black
                    ),
                    modifier = Modifier.background(FondoApp),
                    label = { Text(text = item, fontSize = 18.sp, color = Color.Black) },
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
        Spacer(modifier = Modifier.weight(1f)) // Para poner la opción de Cerrar sesión abajo de drawer
        HorizontalDivider()
        NavigationDrawerItem(
            modifier = Modifier.background(Color.Transparent),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = FondoApp,
                unselectedContainerColor = FondoApp,
                selectedIconColor = Color.Black,
                unselectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                unselectedTextColor = Color.Black
            ),
            label = { Text(text = "Cerrar sesión") },
            selected = false,
            onClick = {
                showCerrarSesionDialog.value = true
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
        modifier = Modifier.background(FondoApp)
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            value = "Información",
            onValueChange = {},
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.information),
                    contentDescription = "Icono información",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            },
            trailingIcon = { TrailingIconMio(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(FondoOpcionesSubMenu)
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = asignaLogoSegunOpcion(selectionOption)),
                            contentDescription = "Icono opción",
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    text = { Text(selectionOption, fontSize = 17.sp, color = Color.Black) },
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
private fun asignaLogoSegunOpcion(opcion: String): Int {
    return when (opcion) {
        "¿Qué es Regala Navidad?" -> R.drawable.question
        "Datos y objetivos" -> R.drawable.target
        "¿Cómo puedo ayudar?" -> R.drawable.help
        "Contáctanos" -> R.drawable.contact_icon
        "Patrocinadores" -> R.drawable.patrocinadores_icon
        "Otros años" -> R.drawable.calendar
        else -> R.drawable.voluntario_icono
    }
}

@Composable
fun ObserveNavigationChanges(
    navController: NavController,
    onNavigated: (String) -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val lifecycle = currentBackStackEntry?.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                currentBackStackEntry?.destination?.route?.let { route ->
                    onNavigated(route)
                }
            }
        }
        lifecycle?.addObserver(observer)
        onDispose {
            lifecycle?.removeObserver(observer)
        }
    }
}