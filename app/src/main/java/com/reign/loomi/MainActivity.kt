package com.reign.loomi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reign.loomi.navigation.LoomiNavGraph
import com.reign.loomi.ui.theme.LoomiTheme
import com.reign.loomi.viewmodel.LoomiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val loomiViewModel: LoomiViewModel = viewModel()
            val uiState by loomiViewModel.uiState.collectAsStateWithLifecycle()

            LoomiTheme(themeId = uiState.currentThemeId) {
                LoomiNavGraph(viewModel = loomiViewModel)
            }
        }
    }
}
