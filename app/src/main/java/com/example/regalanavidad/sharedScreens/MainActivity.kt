package com.example.regalanavidad.sharedScreens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.regalanavidad.loginSignUp.Login
import com.example.regalanavidad.loginSignUp.SignUpScreen
import com.example.regalanavidad.loginSignUp.WaitForEmailVerificationScreen
import com.example.regalanavidad.loginSignUp.PantallaOlvidoContrasena
import com.example.regalanavidad.ui.theme.RegalaNavidadTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    companion object{
        var email: String = ""
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        setContent {
            RegalaNavidadTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController, "inicio"){
                        composable("inicio"){
                            Login(navController, auth)
                        }
                        composable("recuperaPassword"){
                            PantallaOlvidoContrasena(navController, auth)
                        }
                        composable("registroCuenta"){
                            SignUpScreen(navController, auth)
                        }
                        composable("waitingScreen"){
                            WaitForEmailVerificationScreen(auth)
                        }
                    }
                }
            }
        }
    }
}