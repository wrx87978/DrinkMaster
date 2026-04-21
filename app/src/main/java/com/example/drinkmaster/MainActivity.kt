package com.example.drinkmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.drinkmaster.ui.theme.DrinkMasterTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DrinkMasterTheme {
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser

                if (currentUser == null) {
                    AuthScreen(
                        onAuthSuccess = {
                            recreate()
                        }
                    )
                } else {
                    MainScreen(
                        currentUserEmail = currentUser.email ?: "",
                        onLogout = {
                            auth.signOut()
                            recreate()
                        }
                    )
                }
            }
        }
    }
}