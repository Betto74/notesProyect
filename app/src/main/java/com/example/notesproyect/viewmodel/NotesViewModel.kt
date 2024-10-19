package com.example.notesproyect.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


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
