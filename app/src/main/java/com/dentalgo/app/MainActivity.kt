package com.dentalgo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dentalgo.app.core.data.SessionManager
import com.dentalgo.app.core.ui.navigation.DentalGoNavGraph
import com.dentalgo.app.ui.theme.DentalGoTheme

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sessionManager = SessionManager(this)

        setContent {
            DentalGoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    DentalGoNavGraph(
                        navController  = navController,
                        sessionManager = sessionManager
                    )
                }
            }
        }
    }
}
