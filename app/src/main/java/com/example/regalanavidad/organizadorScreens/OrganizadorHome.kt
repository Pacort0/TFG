package com.example.regalanavidad.organizadorScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.regalanavidad.R
import com.example.regalanavidad.sharedScreens.InformacionSubMenu
import com.example.regalanavidad.sharedScreens.ProfileScreen
import com.example.regalanavidad.sharedScreens.ScreenContent
import com.example.regalanavidad.sharedScreens.ShowDialog
import com.example.regalanavidad.sharedScreens.TabBarItem
import com.example.regalanavidad.sharedScreens.TabView
import com.example.regalanavidad.sharedScreens.checkIfCanManageEmails
import com.example.regalanavidad.sharedScreens.drawerAbierto
import com.example.regalanavidad.sharedScreens.drawerItems
import com.example.regalanavidad.sharedScreens.tareasVM
import com.example.regalanavidad.sharedScreens.usuario
import com.example.regalanavidad.ui.theme.RegalaNavidadTheme
import com.example.regalanavidad.viewmodels.mapaOrganizadorVM
import com.example.regalanavidad.voluntarioScreens.ComoAyudar
import com.example.regalanavidad.voluntarioScreens.ContactanosScreen
import com.example.regalanavidad.voluntarioScreens.DatosYObjetivos
import com.example.regalanavidad.voluntarioScreens.OtrosAnosScreen
import com.example.regalanavidad.voluntarioScreens.PatrocinadoresScreen
import com.example.regalanavidad.voluntarioScreens.QueEs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizadorHomeScreen(mapaAbierto: Boolean, mapaOrganizadorVM: mapaOrganizadorVM, onMapaCambiado: (Boolean) -> Unit) {
    var currentTabTitle by remember { mutableStateOf("Home") }
    val homeTab = TabBarItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    val tareasTab = TabBarItem(
        title = "Tareas",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
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
    var tabBarItems = listOf(homeTab, tareasTab, mapsTab)
    if (usuario.nombreRango == "Coordinador"){
        tabBarItems = listOf(homeTab, tareasTab, mapsTab, rolesTab, sheetsTab)
    }
    if (usuario.nombreRango == "Tesorería" || usuario.nombreRango == "Secretaría" || usuario.nombreRango == "RR.II."){
        tabBarItems = listOf(homeTab, tareasTab, mapsTab, sheetsTab)
    }
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
                                title = { Text(text = currentTabTitle, fontSize = 25.sp) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                        mapaOrganizadorVM.searchSitioRecogida.value = false
                                    }) {
                                        Icon(Icons.Filled.Menu, contentDescription = "Localized description")
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
                                        )
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            TabView(tabBarItems, navController, mapaOrganizadorVM) { title ->
                                currentTabTitle = title
                            }
                        },
                        content = { innerPadding -> //NavHost
                            OrganizadorNavHost(innerPadding, navController, homeTab, tareasTab, mapsTab, rolesTab, sheetsTab, onMapaCambiado, mapaOrganizadorVM)
                        }
                    )
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OrganizadorNavHost(innerPadding : PaddingValues, navController: NavHostController, homeTab: TabBarItem, alertsTab: TabBarItem, mapsTab: TabBarItem, rolesTab: TabBarItem, sheetsTab: TabBarItem, onMapaCambiado: (Boolean) -> Unit, mapaOrganizadorVM: mapaOrganizadorVM){
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
            if(usuario.nombreRango == "Tesorería" || usuario.nombreRango == "Secretaría" || usuario.nombreRango == "RR.II." || usuario.nombreRango == "Coordinador"){
                composable(sheetsTab.title) {
                    ScreenContent(screenTitle = sheetsTab.title, navController = navController, onMapaCambiado = onMapaCambiado, mapaOrganizadorVM = mapaOrganizadorVM)
                }
            }
            composable("PagContactosCentrosEdu"){
                PaginaContactosCentrosEducativos(navController)
            }
            composable("SheetCentrosEducativos"){
                PaginaSheetCentrosEducativos(navController)
            }
            composable("SheetGastos"){
                PaginaSheetGastos()
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
        Spacer(modifier = Modifier.weight(1f)) // Para poner la opción de Cerrar sesión abajo de drawer
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




