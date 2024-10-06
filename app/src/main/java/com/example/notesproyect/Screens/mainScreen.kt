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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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


@Composable
fun principal(){
    var search by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                modifier = Modifier.padding(start = 15.dp, top = 20.dp, end = 15.dp).fillMaxWidth()
            )

            LazyColumn {
                items(10) { index ->
                    viewNote("NOTA1", "PsadfasjkljflkdsjflsdkjflkdsjgfklsdjflksdjflksdjflksdjfEPE", "10/12/12")
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 40.dp) // Aplica padding aquí
        ) {

            desplegarOpciones()
        }
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
    Box( modifier =  Modifier.padding(15.dp).clip(RoundedCornerShape(16.dp)).background(Color.Gray)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f).padding(15.dp)) {
                Text(titulo, style = MaterialTheme.typography.titleLarge)
                //Spacer(modifier = Modifier.height(1.dp))
                Text(descripcion, style =  MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
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
                modifier = Modifier.fillMaxWidth(), // Permite que la columna use todo el ancho
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
    principal()
}