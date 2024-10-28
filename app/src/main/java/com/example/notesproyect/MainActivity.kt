package com.example.notesproyect

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notesproyect.ui.theme.NotesProyectTheme
import com.example.notesproyect.Screens.Principal
import com.example.notesproyect.navegacion.AppNavigation
import com.example.notesproyect.viewmodel.NotesViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    private val viewModel: NotesViewModel by viewModels() // Instancia del ViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            NotesProyectTheme {
                AppNavigation()

            }
        }
    }
}
