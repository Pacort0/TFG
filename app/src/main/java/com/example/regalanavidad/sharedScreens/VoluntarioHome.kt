package com.example.regalanavidad.sharedScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.regalanavidad.modelos.TabBarItem
import com.example.regalanavidad.organizadorScreens.ModalItems
import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.RegalaNavidadTheme
import com.example.regalanavidad.viewmodels.MapaVM
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoluntarioHomeScreen(mapaAbierto: Boolean, mapaOrganizadorVM: MapaVM, onMapaCambiado: (Boolean) -> Unit) {
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
    val tabBarItems = listOf(homeTab, mapsTab)
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
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = ColorLogo
                                ),
                                title = { Text(text = currentTabTitle, fontSize = 25.sp, color = Color.Black) },
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
                                            Modifier,
                                            Color.Black
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
                            VoluntarioNavHost(innerPadding, navController, homeTab, mapsTab, onMapaCambiado, mapaOrganizadorVM)
                        }
                    )
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun VoluntarioNavHost(innerPadding : PaddingValues, navController: NavHostController, homeTab: TabBarItem, mapsTab: TabBarItem, onMapaCambiado: (Boolean) -> Unit, mapaOrganizadorVM: MapaVM) {
    Box(modifier = Modifier.padding(innerPadding)) {
        NavHost(
            navController = navController,
            startDestination = homeTab.title
        ) {
            composable(homeTab.title) {
                ScreenContent(
                    screenTitle = homeTab.title,
                    navController = navController,
                    onMapaCambiado = onMapaCambiado,
                    mapaOrganizadorVM = mapaOrganizadorVM
                )
            }
            composable(mapsTab.title) {
                ScreenContent(
                    screenTitle = mapsTab.title,
                    navController = navController,
                    onMapaCambiado = onMapaCambiado,
                    mapaOrganizadorVM = mapaOrganizadorVM
                )
            }
            composable("profileScreen") {
                ProfileScreen()
            }
            composable("QueEsScreen") {
                QueEs()
            }
            composable("ComoAyudarScreen") {
                ComoAyudar()
            }
            composable("DatosYObjetivosScreen") {
                DatosYObjetivos()
            }
            composable("ContactanosScreen") {
                ContactanosScreen()
            }
            composable("PatrocinadoresScreen") {
                PatrocinadoresScreen()
            }
            composable("OtrosAnosScreen") {
                OtrosAnosScreen()
            }
        }
    }
}