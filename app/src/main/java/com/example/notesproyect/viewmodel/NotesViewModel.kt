package com.example.notesproyect.viewmodel

import androidx.lifecycle.ViewModel
import com.example.notesproyect.data.Nota
import com.example.notesproyect.data.TipoNota
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class NotesViewModel : ViewModel() {
    private val _titulo = MutableStateFlow("")
    val titulo : StateFlow<String> = _titulo

    private val _descripcion = MutableStateFlow("")
    val descripcion : StateFlow<String> = _descripcion

    private val _buscar = MutableStateFlow("")
    val buscar : StateFlow<String> = _buscar

    private val _notas = MutableStateFlow<List<Nota>>(emptyList())
    val notas: StateFlow<List<Nota>> get() = _notas

    private val _tareas = MutableStateFlow<List<Nota>>(emptyList())
    val tareas: StateFlow<List<Nota>> get() = _tareas

    fun onSearchChange(search:String, tipo : TipoNota){
        _buscar.value = search
        if( tipo == TipoNota.NOTA ){
            filteredNotes(_notas.value)
        }
        else{
            filteredNotes(_tareas.value)
        }
    }

    fun filteredNotes(tipo : List<Nota>): List<Nota> {
        return tipo.filter { note ->
            note.titulo.contains(_buscar.value, ignoreCase = true) ||
                    note.descripcion.contains(_buscar.value, ignoreCase = true)
        }
    }
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

* */