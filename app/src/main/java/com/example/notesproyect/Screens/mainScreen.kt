package com.example.notesproyect.Screens

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notesproyect.R
import com.example.notesproyect.data.Nota
import com.example.notesproyect.data.TipoNota
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Principal() {
    var search by remember { mutableStateOf("") }
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse("20/10/2023") ?: Date()
    val notes = List(10) { Nota(TipoNota.NOTA,"Titulo", "Descriasdasd\npcion", formatoFecha) }

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

                textBox(
                    label = "Buscar",
                    trailingIcon = R.drawable.search,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    value = search,
                    onValueChanged = { search = it },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp).fillMaxWidth()
                )
                NoteList(notes)
            }
            BottomFloatingButton(onClick = {}, Modifier.align(Alignment.BottomEnd))
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
fun SearchBar(search: String, onSearchChange: (String) -> Unit) {
    textBox(
        label = "Buscar",
        trailingIcon = R.drawable.search,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        value = search,
        onValueChanged = onSearchChange,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(start = 15.dp, end = 15.dp).fillMaxWidth()
    )
}

@Composable
fun NoteList(notes: List<Nota>) {
    LazyColumn(
    ) {
        items(notes.size) { index ->
            val note = notes[index]
            viewNote(note.titulo, note.descripcion, note.fechaCreacion.toString())
        }
        item {
            Spacer(modifier = Modifier.height(100.dp)) // Espacio adicional al final
        }
    }


}
@Composable
fun BottomFloatingButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(bottom = 70.dp, end = 40.dp)
    ) {
        desplegarOpciones()
    }
}

@Composable
fun desplegarOpciones(
    modifier: Modifier = Modifier
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
                TextButton(onClick = { expanded = false })
                {
                    Text("Agregar nota")
                }
                TextButton(onClick = { expanded = false }) {
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
@Composable
fun viewNote(
    titulo : String,
    descripcion : String,
    fecha : String,

    ){
    Box( modifier =  Modifier.padding(start = 15.dp, end = 15.dp, top=10.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer)) {
        Row(modifier = Modifier.fillMaxWidth()) {
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
                TextButton(onClick = {
                    expanded = false
                }) {
                    Text("Editar")
                }
                TextButton(onClick = {
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
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape
){
    TextField(
        value = value,
        trailingIcon = trailingIcon?.let {
            { Icon(painter = painterResource(id = it), contentDescription = null, modifier = Modifier.size(24.dp)) }
        },
        shape = shape,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(label) },
        keyboardOptions = keyboardOptions
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    Principal()
}