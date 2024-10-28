package com.example.notesproyect.Screens

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.navigation.NavController
import com.example.notesproyect.R
import com.example.notesproyect.data.Nota
import com.example.notesproyect.data.TipoNota
import com.example.notesproyect.navegacion.AppScreen
import com.example.notesproyect.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Principal(viewModel: NotesViewModel, navController: NavController) {
   // var search by remember { mutableStateOf("") }
    val  buscar : String by viewModel.buscar.collectAsState()
    val lista: List<Nota> by viewModel.listaFiltradas.collectAsState()




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
                SearchBar(buscar,{viewModel.onSearchChange(it)}, Modifier.padding(start = 15.dp, end = 15.dp, bottom = 5.dp).fillMaxWidth(),)
                NoteList(lista,viewModel)

            }
            BottomFloatingButton(Modifier.align(Alignment.BottomEnd), navController)
           // BottomButtons(Modifier.align(Alignment.BottomCenter))B
            BottomNav(Modifier.align(Alignment.BottomCenter), viewModel)

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopBar(
    tittle : String
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
    textBox(
        label = "Buscar",
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
fun NoteList(notes: List<Nota>,viewModel: NotesViewModel) {

    LazyColumn(

    ) {
        items(notes.size) { index ->
            val note = notes[index]
            viewNote(note.titulo, note.descripcion, note.fechaCreacion.toString(),viewModel)
        }
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
fun BottomNav(modifier: Modifier = Modifier, viewModel: NotesViewModel){

    val  selectedTabIndex : Int by viewModel.selectedTabIndex.collectAsState()

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier
       // modifier = modifier.align(Alignment.BottomCenter).background(MaterialTheme.colorScheme.primary)
        //backgroundColor = MaterialTheme.colorScheme.primary
    ) {

        Tab(
            selected = selectedTabIndex == 0,
            modifier = Modifier.height(50.dp),
            onClick = { viewModel.selectTab(0)}

        ) {
            Row() {
                Icon(
                    painter = painterResource(R.drawable.note),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp) // Tamaño del ícono
                )
                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre ícono y texto
                Text("Notas", color = Color.White)
            }
        }
        Tab(
            selected = selectedTabIndex == 1,
            modifier = Modifier.height(50.dp),
            onClick = { viewModel.selectTab(1) }
        ) {
            Row() {
                Icon(
                    painter = painterResource(R.drawable.task),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp) // Tamaño del ícono
                )
                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre ícono y texto
                Text("Tareas", color = Color.White)
            }
        }

    }

}

@Composable
fun BottomButtons(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)
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
                            text = "Tarea", // Cambia este texto según sea necesario
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
                            text = "Nota", // Cambia este texto según sea necesario
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

    Column(modifier = modifier){

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
                        navController.navigate(route = AppScreen.SecondScreen.route+"/"+TipoNota.NOTA)
                    } )
                {
                    Text("Agregar nota")
                }
                TextButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        expanded = false
                        navController.navigate(route = AppScreen.SecondScreen.route+"/"+TipoNota.TAREA)
                    }) {
                    Text("Agregar tarea")
                }
            }
        }
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.Black)
        ) {
            Icon(
                painter = painterResource(R.drawable.plus),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(35.dp) // Tamaño del icono
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun viewNote(
    titulo : String,
    descripcion : String,
    fecha : String,
    viewModel: NotesViewModel
    ){


    Box( modifier =  Modifier.padding(start = 15.dp, end = 15.dp, top=10.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer)) {
        var isChecked by remember { mutableStateOf(false) }
        val index: Int by viewModel.selectedTabIndex.collectAsState()
        Row(modifier = Modifier.fillMaxWidth()) {

            if( index==1 ){
                Checkbox(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    checked = isChecked,
                    onCheckedChange = { isChecked = it } // Actualiza el estado al hacer clic
                )
            }

            Column(modifier = Modifier.weight(1f).padding(15.dp)) {
                Text(titulo, style = MaterialTheme.typography.titleLarge)
                //Spacer(modifier = Modifier.height(1.dp))
                Text(descripcion, style =  MaterialTheme.typography.bodyLarge)
                //Spacer(modifier = Modifier.height(2.dp))
                Text(fecha)
            }

            desplegarAE()


        }
    }
}

@Composable
fun desplegarAE(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        IconButton(onClick = { expanded = true }){
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
                    expanded = false
                }) {
                    Text("Editar")
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
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
    @DrawableRes trailingIcon: Int ? = null,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    onValueChange:(String) -> Unit
){
    TextField(
        value = value,
        trailingIcon = trailingIcon?.let {
            { Icon(painter = painterResource(id = it), contentDescription = null, modifier = Modifier.size(24.dp)) }
        },
        shape = shape,
        singleLine = true,
        modifier = modifier,
        onValueChange = {onValueChange(it)},
        label = { Text(label) },
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    //Principal(NotesViewModel())
}