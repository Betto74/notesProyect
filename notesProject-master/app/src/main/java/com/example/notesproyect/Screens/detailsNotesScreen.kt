package com.example.notesproyect.Screens

import com.example.notesproyect.Screens.MostrarMedia

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notesproyect.R
import com.example.notesproyect.data.Note
import com.example.notesproyect.ui.AppViewModelProvider
import com.example.notesproyect.viewmodel.NotesViewModel
import androidx.compose.ui.res.stringResource
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsNoteScreen(
    navController: NavController,
    id: Int?, // Recibe la nota como parámetro
    viewModel: NotesViewModel = viewModel(factory = AppViewModelProvider.Factory) // Añadir el ViewModel
) {
    Text("hola")
    id?.let { viewModel.fetchNoteById(it) }
    val note = viewModel.currentNote.collectAsState().value

    details(navController,note)
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun details(
    navController: NavController,
    note: Note?
){
    // Comprobamos si la nota es nula
    note?.let {
        Scaffold(
            topBar = {
                TopAppBar(

                    title = { Text(stringResource(id = R.string.noteDetails)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(painter = painterResource(R.drawable.arrow_back), contentDescription = "Atrás")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {



                Text("${stringResource(id = R.string.title)}: ${it.titulo}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("${stringResource(id = R.string.description)}: ${it.descripcion}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("${stringResource(id = R.string.creationDate)}: ${it.fechaCreacion}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("${stringResource(id = R.string.expirationDate)}: ${it.fechaVencimiento}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("${stringResource(id = R.string.done)}: ${it.hecha}", style = MaterialTheme.typography.bodyLarge)
                MostrarMedia(viewModel = viewModel(),false, it.tipo == TipoNota.TAREA.name)
            }
        }
    } ?: run {
        // Manejo de caso en que la nota sea nula
        Text(text = stringResource(id = R.string.noteNotFound))
    }
}

