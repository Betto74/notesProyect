package com.example.notesproyect.data

import android.util.Log
import kotlinx.coroutines.flow.Flow


interface NotesRepository {
    // Notas

    fun getAllNotesStream(): Flow<List<Note>>
    fun getAllTasksStream(): Flow<List<Note>>
    fun getNoteStream(id: Int): Flow<Note?>
    suspend fun insertNote(item: Note) : Long
    suspend fun deleteNote(item: Note)
    suspend fun updateNote(item: Note)

    // Recordatorios
    /*fun getRemindersForNoteStream(noteId: Int): Flow<List<Reminder>>
    suspend fun insertReminder(item: Reminder) : Long
    suspend fun deleteReminder(item: Reminder)
    // Eliminar notificaciones
    suspend fun deleteNotificationsByTaskId(taskId: Int)*/
}
