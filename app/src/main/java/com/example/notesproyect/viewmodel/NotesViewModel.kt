package com.example.notesproyect.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.notesproyect.data.Nota
import com.example.notesproyect.data.TipoNota
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class NotesViewModel : ViewModel() {

    /*****************************************/
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> get() = _selectedTabIndex

    // Función para cambiar la pestaña seleccionada
    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
        if( index == 0 )applyFilter(TipoNota.NOTA)
        else applyFilter(TipoNota.TAREA)
    }

    /*****************************************/

    /********** TextBox Notas *********/
    private val _titulo = MutableStateFlow("")
    val titulo : StateFlow<String> get() = _titulo

    private val _descripcion = MutableStateFlow("")
    val descripcion : StateFlow<String> get() = _descripcion


    private val _fecha = MutableStateFlow(LocalDate.now())
    val fecha: StateFlow<LocalDate> get() = _fecha

    private val _horas = MutableStateFlow(currentHours())
    val horas: StateFlow<Int> get() = _horas

    private val _minutos = MutableStateFlow(currentMinutes())
    val minutos: StateFlow<Int> get() = _minutos

    fun updateTitle(newTitle: String) {
        _titulo.value = newTitle
    }

    fun updateDescription(newDescription: String) {
        _descripcion.value = newDescription
    }
    fun updateDate(newDate: LocalDate) {
        _fecha.value = newDate
    }
    fun updateTime(newH : Int,newM : Int) {
        _horas.value = newH
        _minutos.value = newM
    }

    fun saveNote(tipo: TipoNota) {

        val nuevaNota = Nota(
            tipo = tipo,
            titulo = _titulo.value,
            descripcion = _descripcion.value,
            fechaCreacion = Date(), // O la fecha que desees usar
            fechaVencimiento = fechaConHora()

        )
        if( tipo == TipoNota.TAREA ){
            _tareas.value = _tareas.value + nuevaNota

        }
        else{
            _notas.value = _notas.value + nuevaNota

        }

        if( _selectedTabIndex.value == 0)applyFilter(TipoNota.NOTA)
        else applyFilter(TipoNota.TAREA)

        Log.d("NotesViewModel", "Nota guardada: $nuevaNota") // Registro para verificar que se guarda la nota
        Log.d("NotesViewModel", "Total de notas: ${_notas.value.size}")

        _titulo.value = ""
        _descripcion.value = ""

    }

    // Funciones para obtener la fecha y hora actuales
    private fun fechaConHora(): LocalDateTime{
        val fechaHora = LocalDateTime.of(_fecha.value, LocalTime.of(_horas.value, _minutos.value))
        return fechaHora
    }

    private fun currentHours(): Int {
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.hour
    }

    private fun currentMinutes(): Int {
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.minute
    }

    /********** TextBox Notas *********/

    /************ Pantalla principal *******************/

    private val _buscar = MutableStateFlow("")
    val buscar : StateFlow<String> get() = _buscar

    private val _notas = MutableStateFlow<List<Nota>>(emptyList())
    val notas: StateFlow<List<Nota>> get() = _notas

    private val _tareas = MutableStateFlow<List<Nota>>(emptyList())
    val tareas: StateFlow<List<Nota>> get() = _tareas

    private val _listaFiltradas = MutableStateFlow<List<Nota>>(emptyList())
    val listaFiltradas: StateFlow<List<Nota>> get() = _listaFiltradas

    fun onSearchChange(search:String){
        _buscar.value = search
        if( _selectedTabIndex.value==0 )applyFilter(TipoNota.NOTA)
        else applyFilter(TipoNota.TAREA)

    }

    private fun applyFilter(tipo : TipoNota) {
        val listaVisible = if (tipo == TipoNota.NOTA) _notas.value else _tareas.value
        _listaFiltradas.value = if (_buscar.value.isBlank()) listaVisible else {
            listaVisible.filter { it.titulo.contains(_buscar.value, ignoreCase = true) || it.descripcion.contains(_buscar.value, ignoreCase = true) }
        }
    }
    /************ Pantalla principal *******************/






}




/*
*
*
class NotesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState


    fun addNote(note: String) {
        _uiState.value = _uiState.value.copy(
            notes = _uiState.value.notes + note
        )
    }
}


data class NotesUiState(
    val notes: List<String> = emptyList() // Lista de notas como parte del estado.
)
===============
*
* fun filteredNotes(tipo : List<Nota>): List<Nota> {
        return tipo.filter { note ->
            note.titulo.contains(_buscar.value, ignoreCase = true) ||
                    note.descripcion.contains(_buscar.value, ignoreCase = true)
        }
    }
* */