package com.example.regalanavidad

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.ui.theme.RegalaNavidadTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

val drawerItems = listOf("Contáctanos", "ejemplo1", "ejemplo2")
val auth = Firebase.auth
var usuario = Usuario()
val firestore = FirestoreManager()


class Home : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        var currentTabTitle by mutableStateOf("Home")
        val correo = intent.getStringExtra("correo")

        runBlocking {
            val task = launch {
                usuario = correo?.let { firestore.findUserByEmail(it) }!!
            }
            task.join()
        }

        super.onCreate(savedInstanceState)
        setContent {
            // setting up the individual tabs
            val homeTab = TabBarItem(
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            )
            val alertsTab = TabBarItem(
                title = "Alerts",
                selectedIcon = Icons.Filled.Notifications,
                unselectedIcon = Icons.Outlined.Notifications,
                badgeAmount = 0
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
            val tabBarItems = listOf(homeTab, alertsTab, settingsTab, moreTab)

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
                            ModalDrawerSheet {
                                Text("Regala Navidad", modifier = Modifier.padding(16.dp))
                                Divider()
                                drawerItems.forEach { item ->
                                    NavigationDrawerItem(
                                        label = { Text(text = item) },
                                        selected = false, // Set the selected state as needed
                                        onClick = { /* Handle item click here */ }
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
                                }
                                ,
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
                                                ScreenContent(
                                                    screenTitle = homeTab.title,
                                                    navController = navController
                                                )
                                            }
                                            composable(alertsTab.title) {
                                                ScreenContent(
                                                    screenTitle = alertsTab.title,
                                                    navController = navController
                                                )
                                            }
                                            composable(settingsTab.title) {
                                                ScreenContent(
                                                    screenTitle = settingsTab.title,
                                                    navController = navController

                                                )
                                            }
                                            composable(moreTab.title) {
                                                ScreenContent(
                                                    screenTitle = moreTab.title,
                                                    navController = navController
                                                )
                                            }
                                            composable("profileScreen"){
                                                ProfileScreen(
                                                    navController = navController
                                                )
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
@OptIn(ExperimentalMaterial3Api::class)
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
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}

@Composable
private fun ScreenContent(modifier: Modifier = Modifier, screenTitle: String, navController: NavController) {
    when (screenTitle){
        "Home" -> HomeScreen(modifier)
        "Alerts" -> AlertsScreen(modifier = modifier)
        "Settings" -> SettingsScreen(modifier = modifier)
        "More" -> MoreTabsScreen(modifier = modifier)
    }
}

@Composable
fun HomeScreen(modifier: Modifier){
    auth.currentUser?.reload() // Recargamos el usuario para comprobar cualquier actualización
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello ${usuario.nombre}!",
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(15.dp))
        ClickableText(
            text = AnnotatedString("Cerrar sesión"),
            onClick = {
                auth.signOut()
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        )
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
fun SettingsScreen(modifier: Modifier){
    Text(
        text = "Hello ${usuario.correo}!",
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

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    var settingsForm by remember { mutableStateOf(TextFieldValue(usuario.nombre)) }
    var isNameChanged by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3EBEB))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Profile picture
        Image(
            painter = painterResource(id = usuario.pfp),
            contentDescription = "foto de perfil",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(15.dp))

        // Editable username
        OutlinedTextField(
            value = settingsForm,
            onValueChange = {
                settingsForm = it
                isNameChanged = it.text != usuario.nombre
            },
            label = { Text(text = "Nombre de usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = usuario.correo)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = usuario.nombreRango)

        Spacer(modifier = Modifier.height(15.dp))

        // Clickable "Cerrar sesión" text
        ClickableText(
            text = AnnotatedString("Cerrar sesión"),
            onClick = {
                auth.signOut()
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        )

        // Show the Save button if name is changed and not empty
        if (isNameChanged && settingsForm.text.isNotBlank()) {
            Button(
                onClick = {
                    usuario.nombre = settingsForm.text
                    Toast.makeText(context, "Cambiando nombre", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            firestore.editaUsuario(usuario)
                        } catch (e: Exception) {
                            // Handle or log the exception
                            Log.e("ProfileScreen", "Error updating user", e)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "Guardar cambios")
            }
        }
    }
}

/* Si hay tiempo retomamos esta idea
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
