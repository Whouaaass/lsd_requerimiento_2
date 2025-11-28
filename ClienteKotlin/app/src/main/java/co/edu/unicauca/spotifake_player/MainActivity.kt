package co.edu.unicauca.spotifake_player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import co.edu.unicauca.spotifake_player.ui.screens.StreamingPlayerScreen
import co.edu.unicauca.spotifake_player.ui.theme.SpotifakePlayerAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpotifakePlayerAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StreamingPlayerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}