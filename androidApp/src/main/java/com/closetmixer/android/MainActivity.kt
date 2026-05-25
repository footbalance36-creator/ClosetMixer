package com.closetmixer.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.closetmixer.android.ui.navigation.AppNavigation
import com.closetmixer.android.ui.theme.ClosetMixerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClosetMixerTheme {
                AppNavigation()
            }
        }
    }
}
