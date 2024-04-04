package com.example.regalanavidad

import MapsScreen
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.organizadorScreens.OrganizadorHomeScreen
import com.example.regalanavidad.voluntarioScreens.VoluntarioHomeScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
            var mapaAbierto by remember { mutableStateOf(false) }

            if(esVoluntario){
                VoluntarioHomeScreen(mapaAbierto){
                    abierto -> mapaAbierto = abierto
                }
            } else {
                OrganizadorHomeScreen(mapaAbierto){
                        abierto -> mapaAbierto = abierto
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
                    .padding(0.dp, 0.dp, 5.dp, 0.dp)) {
                    Column {
                        Text(text = "Dinero recaudado: 1€")
                    }
                }
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp, 0.dp, 0.dp, 0.dp)) {
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
                    .padding(0.dp, 0.dp, 5.dp, 0.dp)) {
                    Column {
                        Text(text = "Fechas y eventos")
                    }
                }
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp, 0.dp, 0.dp, 0.dp)) {
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
