package com.example.regalanavidad.voluntarioScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.regalanavidad.AlertsScreen
import com.example.regalanavidad.HomeScreen
import com.example.regalanavidad.MoreTabsScreen
import com.example.regalanavidad.ProfileScreen
import com.example.regalanavidad.ScreenContent
import com.example.regalanavidad.SettingsScreen
import com.example.regalanavidad.TabBarItem
import com.example.regalanavidad.TabView
import com.example.regalanavidad.drawerItems
import com.example.regalanavidad.ui.theme.RegalaNavidadTheme
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
    val settingsTab = TabBarItem(
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
    val moreTab = TabBarItem(
        title = "More",
        selectedIcon = Icons.AutoMirrored.Filled.List,
        unselectedIcon = Icons.AutoMirrored.Outlined.List
    )
    val tabBarItems = listOf(homeTab, settingsTab, moreTab)

    val navController = rememberNavController()
    NavHost(
        startDestination = "homeScreen",
        navController = navController
    ) {
        composable("homeScreen") {
            HomeScreen(modifier = Modifier)
        }
        composable("alertsScreen") {
            AlertsScreen(modifier = Modifier)
        }
        composable("settingsScreen") {
            SettingsScreen(modifier = Modifier)
        }
        composable("moreTabsScreen") {
            MoreTabsScreen(modifier = Modifier)
        }
    }
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
                    ModalDrawerSheet {
                        Text("Regala Navidad", modifier = Modifier.padding(16.dp))
                        Divider()
                        drawerItems.forEach { item ->
                            NavigationDrawerItem(
                                label = { Text(text = item) },
                                selected = false, // Set the selected state as needed
                                onClick = {
                                    when (item) {
                                        "Informacion" -> navController.navigate("homeScreen")
                                        "Contactanos" -> navController.navigate("alertsScreen")
                                        "Patrocinadores" -> navController.navigate("settingsScreen")
                                        "Otros años" -> navController.navigate("moreTabsScreen")
                                    }
                                }
                            )
                        }
                    }
                },
                content = {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(text = currentTabTitle) },
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
                        content = { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                NavHost(
                                    navController = navController,
                                    startDestination = homeTab.title
                                ) {
                                    composable(homeTab.title) {
                                        ScreenContent(screenTitle = homeTab.title)
                                    }
                                    composable(settingsTab.title) {
                                        ScreenContent(screenTitle = settingsTab.title)
                                    }
                                    composable(moreTab.title) {
                                        ScreenContent(screenTitle = moreTab.title)
                                    }
                                    composable("profileScreen"){
                                        ProfileScreen(navController = navController)
                                    }
                                }
                            }
                        }
                    )
                }
            )
        }
    }
}