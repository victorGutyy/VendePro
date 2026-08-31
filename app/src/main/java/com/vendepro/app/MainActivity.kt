package com.vendepro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vendepro.app.ui.navigation.VendeProNavHost
import com.vendepro.app.ui.theme.VendeProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VendeProTheme {
                VendeProNavHost()
            }
        }
    }
}
