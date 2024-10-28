package com.example.notesproyect.Screens

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesproyect.R
import com.example.notesproyect.data.TipoNota
import com.example.notesproyect.navegacion.AppNavigation
import com.example.notesproyect.navegacion.AppScreen
import com.example.notesproyect.viewmodel.NotesViewModel
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notas( viewModel: NotesViewModel , navController: NavController, text: String?) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground



    val  titulo : String by viewModel.titulo.collectAsState()
    val  descripcion : String by viewModel.descripcion.collectAsState()

    Scaffold(
        topBar = {
            Top(navController,text,viewModel)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues).background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {


            InputField(label = "Título", value = titulo, onValueChanged = {viewModel.updateTitle(it)})

            InputField(label = "Descripción", value = descripcion, onValueChanged = { viewModel.updateDescription(it)})


            TextButton(onClick = {}, modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
                Text("Agregar archivo", style = MaterialTheme.typography.titleMedium)
            }
            if( text == TipoNota.TAREA.toString() ) {
                TextButton(onClick = {}, modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
                    Text("Agregar notificacion", style = MaterialTheme.typography.titleMedium)
                }
            }

        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Top(navController: NavController,text:String?, viewModel: NotesViewModel){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {navController.popBackStack()}) {
                Icon(painter = painterResource(R.drawable.cancel), contentDescription = "Cancelar",modifier = Modifier.size(24.dp))
            }
        },
        title = {
            Box(modifier =  Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if( text == TipoNota.NOTA.toString() )Text("Nueva nota", style = MaterialTheme.typography.titleMedium)
                else Text("Nueva tarea", style = MaterialTheme.typography.titleMedium)

            }
        },
        actions = {
            val tipo : TipoNota
            if( text.isNullOrBlank() || text == TipoNota.NOTA.toString() )tipo = TipoNota.NOTA
            else tipo = TipoNota.TAREA
            IconButton(onClick = {
                viewModel.saveNote(tipo)
                navController.popBackStack()
            }) {
                Icon(painter = painterResource(R.drawable.check), contentDescription = "Guardar",modifier = Modifier.size(24.dp))
            }
        },
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaHoraPicker(viewModel: NotesViewModel) {

    val  horas : Int by viewModel.horas.collectAsState()
    val  minutos : Int by viewModel.minutos.collectAsState()
    val  fecha : LocalDate by viewModel.fecha.collectAsState()

    val calendarState = rememberSheetState()
    CalendarDialog(
        state = calendarState,
        selection = CalendarSelection.Date { d ->

            viewModel.updateDate(d)
        }
    )


    val clockState = rememberSheetState()

    ClockDialog(
        state = clockState,
        config = ClockConfig(
            is24HourFormat = true
        ),
        selection = ClockSelection.HoursMinutes { h, m ->
            viewModel.updateTime(h,m)
        }
    )

    Row(modifier = Modifier.padding(start = 15.dp, top = 20.dp, end = 15.dp)) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Fecha", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                calendarState.show()
            }) {
                Text(fecha.toString())
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hora", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                clockState.show()
            }) {
                val formattedTime = String.format("%02d:%02d", horas, minutos)
                Text(formattedTime)
            }
        }
    }

}

@Composable
fun InputField(label: String, value: String, onValueChanged: (String) -> Unit) {
    Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 15.dp, top = 20.dp, end = 15.dp).fillMaxWidth())
    textBox(
        label = label,
        value = value,
        onValueChanged = onValueChanged,
        modifier = Modifier.padding(start = 15.dp, end = 15.dp).fillMaxWidth(),
        onValueChange = onValueChanged
    )
}




@Preview( showBackground = true, showSystemUi = true)
@Composable
fun Preview() {

 //   Notas(NotesViewModel())
}

/*
*
@Composable
fun Header(
    backgroundColor: Color,
    contentColor: Color
){
    Row( modifier = Modifier.padding(10.dp, top = 10.dp) ){
        IconButton(onClick = {  }, modifier = Modifier.size(40.dp)){
            Icon(painter = painterResource(R.drawable.cancel), null, modifier = Modifier.size(24.dp))
        }
        Box(modifier = Modifier.weight(1f).background(backgroundColor), contentAlignment = Alignment.Center){
            Text("Nueva nota", style = MaterialTheme.typography.titleSmall, color = contentColor)
        }
        IconButton(onClick = {  },modifier = Modifier.size(40.dp)){
            Icon(painter = painterResource(R.drawable.check), null, modifier = Modifier.size(24.dp))
        }
    }
}

*
* */