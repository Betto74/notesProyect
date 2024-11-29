package com.example.notesproyect.Screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notesproyect.R

import com.example.notesproyect.data.Note
import com.example.notesproyect.navegacion.AppScreen
import com.example.notesproyect.ui.AppViewModelProvider
import com.example.notesproyect.viewmodel.NotesUiState
import com.example.notesproyect.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.notesproyect.data.AudioRecorder
import androidx.compose.material3.NavigationRailItem as NavigationRailItem1

enum class TipoNota {
    NOTA,
    TAREA
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun Principal(
    navController: NavController,
   // viewModel: NotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // var search by remember { mutableStateOf("") }
    val windowSizeClass = calculateWindowSizeClass(activity = LocalContext.current as Activity)
    when (windowSizeClass.widthSizeClass) {

        WindowWidthSizeClass.Compact -> {
            Log.d("WindowSizeClass", "Current size: Compact")
            compactScreen(navController)
        }
        WindowWidthSizeClass.Medium -> {
            Log.d("WindowSizeClass", "Current size: Medium")
            mediumScreen(navController)
        }
        WindowWidthSizeClass.Expanded -> {
            Log.d("WindowSizeClass", "Current size: Expanded")
            expandedScreen(navController)
        }
        else -> {
            Log.d("WindowSizeClass", "Current size: Default to Compact {$windowSizeClass}")
            mediumScreen(navController)
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun compactScreen(
    navController: NavController,
    viewModel: NotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val buscar: String by viewModel.buscar.collectAsState()

    val listaFiltrada by viewModel.listaFiltradas.collectAsState()


    Scaffold(
        topBar = { NotesTopBar("Notes") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Column() {
                // Contenido de cada pestaña
                SearchBar(
                    buscar,
                    { viewModel.onSearchChange(it) },
                    Modifier
                        .padding(start = 15.dp, end = 15.dp, bottom = 5.dp)
                        .fillMaxWidth(),
                )
                NoteList(listaFiltrada, viewModel, navController)

            }
            BottomFloatingButton(Modifier.align(Alignment.BottomEnd), navController)
            // BottomButtons(Modifier.align(Alignment.BottomCenter))B
            BottomNav(Modifier.align(Alignment.BottomCenter), viewModel)

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun mediumScreen(
    navController: NavController,
    viewModel: NotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val buscar: String by viewModel.buscar.collectAsState()
    val listaFiltrada by viewModel.listaFiltradas.collectAsState()

    Scaffold(
        topBar = { NotesTopBar("Notes") }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Columna de la izquierda
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                  // Añadir borde derecho
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))
                NavigationRailContent(viewModel)

            }

            // Columna de la derecha
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                SearchBar(
                    buscar,
                    { viewModel.onSearchChange(it) },
                    Modifier.fillMaxWidth()
                    .padding(bottom = 8.dp)
                )
                NoteList(listaFiltrada, viewModel, navController)
                BottomFloatingButton(Modifier, navController) //arreglar esta madre
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun expandedScreen(
    navController: NavController,
    viewModel: NotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val buscar: String by viewModel.buscar.collectAsState()
    val listaFiltrada by viewModel.listaFiltradas.collectAsState()

    Scaffold(
        topBar = { NotesTopBar("Notes") }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Columna de la izquierda
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                // Añadir borde derecho
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchBar(
                    buscar,
                    { viewModel.onSearchChange(it) },
                    Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                NavigationRailContent(viewModel)
                BottomFloatingButton(Modifier, navController)
            }

            // Columna de la derecha
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(16.dp)
            ) {
                NoteList(listaFiltrada, viewModel, navController)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopBar(
    tittle: String
) {
    TopAppBar(
        title = {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(tittle, style = MaterialTheme.typography.titleMedium)
            }
        }
    )
}

@Composable
fun SearchBar(search: String, onSearchChange: (String) -> Unit, modifier: Modifier) {
    val label = stringResource(id = R.string.search_label)
    textBox(
        label = label,
        trailingIcon = R.drawable.search,
        value = search,
        onValueChanged = onSearchChange,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
        onValueChange = onSearchChange
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteList(listaFiltrada: List<Note>, viewModel: NotesViewModel, navController: NavController) {

    LazyColumn(

    ) {
        items(listaFiltrada) { note ->
            viewNote(
                note,
                navController,
                viewModel
            )

        }
        /*items(notes.size) { index ->
            val note = notes[index]
            viewNote(note.titulo, note.descripcion, note.fechaCreacion.toString(), viewModel)
        }*/
        item {
            Spacer(modifier = Modifier.height(100.dp)) // Espacio adicional al final
        }
    }
}

@Composable
fun BottomFloatingButton(modifier: Modifier = Modifier, navController: NavController) {
    Box(
        modifier = modifier
            .padding(bottom = 90.dp, end = 40.dp)
    ) {
        desplegarOpciones(navController = navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationRailContent(
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex: Int by viewModel.selectedTabIndex.collectAsState()

    NavigationRail(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        NavigationRailItem1(
            selected = selectedTabIndex == 0,
            onClick = { viewModel.selectTab(0) },
            icon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.note),
                        contentDescription = stringResource(id = R.string.notes),
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el ícono y el texto
                    Text(
                        text = stringResource(id = R.string.notes),
                        color = Color.White
                    )
                }
            }
        )

        NavigationRailItem1(
            selected = selectedTabIndex == 1,
            onClick = { viewModel.selectTab(1) },
            icon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.task),
                        contentDescription = stringResource(id = R.string.tasks),
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el ícono y el texto
                    Text(
                        text = stringResource(id = R.string.tasks),
                        color = Color.White
                    )
                }
            }
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNav(modifier: Modifier = Modifier, viewModel: NotesViewModel) {

    val selectedTabIndex: Int by viewModel.selectedTabIndex.collectAsState()

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier
        // modifier = modifier.align(Alignment.BottomCenter).background(MaterialTheme.colorScheme.primary)
        //backgroundColor = MaterialTheme.colorScheme.primary
    ) {

        Tab(
            selected = selectedTabIndex == 0,
            modifier = Modifier
                .height(50.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            onClick = { viewModel.selectTab(0) }

        ) {
            Row() {
                Icon(
                    painter = painterResource(R.drawable.note),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp) // Tamaño del ícono
                )
                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre ícono y texto
                Text(text = stringResource(id = R.string.notes), color = Color.White)
            }
        }
        Tab(
            selected = selectedTabIndex == 1,
            modifier = Modifier
                .height(50.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            onClick = { viewModel.selectTab(1) }
        ) {
            Row() {
                Icon(
                    painter = painterResource(R.drawable.task),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp) // Tamaño del ícono
                )
                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre ícono y texto
                Text(text = stringResource(id = R.string.tasks), color = MaterialTheme.colorScheme.onBackground)
            }
        }

    }

}

@Composable
fun BottomButtons(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp) // Sin padding para que ocupe todo el espacio
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.task),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(35.dp) // Tamaño del icono
                        )
                        Text(
                            text = stringResource(id = R.string.tasks),
                            color = Color.White
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp) // Sin padding para que ocupe todo el espacio
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.note),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(35.dp) // Tamaño del icono
                        )
                        Text(
                            text = stringResource(id = R.string.notes),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun desplegarOpciones(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(150.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                TextButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        expanded = false
                        navController.navigate(route = AppScreen.SecondScreen.route + "/" + TipoNota.NOTA.name + "/-1")
                    })
                {
                    Text(text = stringResource(id = R.string.addNote))
                }
                TextButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        expanded = false
                        navController.navigate(route = AppScreen.SecondScreen.route + "/" + TipoNota.TAREA.name + "/-1")
                    }) {
                    Text(text = stringResource(id = R.string.addTask))
                }
            }
        }
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Icon(
                painter = painterResource(R.drawable.plus),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(35.dp) // Tamaño del icono
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun viewNote(
    nota: Note,
    navController: NavController,
    viewModel: NotesViewModel

) {
    val index: Int by viewModel.selectedTabIndex.collectAsState()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, top = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Navegar a la pantalla de detalles de la nota
                    navController.navigate(
                        route = AppScreen.ThirdScreen.route +
                                "/${nota.id}"
                    )
                }
        ) {



            if(nota.tipo == TipoNota.TAREA.name) {

                var isChecked = nota.hecha ?: false

                Checkbox(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    checked = isChecked,
                    onCheckedChange = { checked ->
                        isChecked = checked // Primero cambia el estado
                        viewModel.updateNoteHecha(nota, isChecked, context)

                    }
                )


                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(15.dp)
                ) {

                    Text(
                        text = nota.titulo,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer, // Cambia el color según el estado
                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None // Aplicar rayado
                    )

                    Text(
                        text = nota.descripcion,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = if (index == 1) {
                            nota.fechaVencimiento ?: "Sin vencimiento"
                        } else {
                            nota.fechaCreacion
                        },
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                    )

                }

                desplegarAE(
                    id = nota.id,
                    tipo= nota.tipo,
                    navController = navController,
                    modifier = Modifier,
                    viewModel = viewModel
                )
            }
            else{
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(15.dp)
                ) {

                    Text(
                        text = nota.titulo,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer, // Cambia el color según el estado

                    )

                    Text(
                        text = nota.descripcion,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,

                    )
                    Text(
                        text = if (index == 1) {
                            nota.fechaVencimiento ?: "Sin vencimiento"
                        } else {
                            nota.fechaCreacion
                        },
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )

                }

                desplegarAE(
                    id = nota.id,
                    tipo = nota.tipo,
                    navController = navController,
                    modifier = Modifier,
                    viewModel = viewModel
                )
            }

        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun desplegarAE(
    id: Int,
    tipo: String,
    viewModel: NotesViewModel,
    navController: NavController,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        IconButton(onClick = { expanded = true }) {
            Icon(painter = painterResource(R.drawable.more), null, modifier = Modifier.size(24.dp))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(150.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(), // Permite que la columna usetodo el ancho
                horizontalAlignment = Alignment.CenterHorizontally // Centra los botones
            ) {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        navController.navigate(AppScreen.SecondScreen.route + "/$tipo/$id")
                        expanded = false
                    }) {
                    Text("Editar")
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.deleteNote(id)
                        expanded = false
                    }) {
                    Text("Eliminar")
                }
            }
        }
    }
}


@Composable
fun textBox(
    label: String,
    @DrawableRes trailingIcon: Int? = null,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        trailingIcon = trailingIcon?.let {
            {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        shape = shape,
        singleLine = true,
        modifier = modifier,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    //Principal(NotesViewModel())
}