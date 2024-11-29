package com.example.notesproyect.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class OfflineNotesRepository(private val noteDao: NoteDao) : NotesRepository {

    //Notas
    override fun getAllNotesStream(): Flow<List<Note>> = noteDao.getAllNotes()
    override fun getAllTasksStream(): Flow<List<Note>> = noteDao.getAllTasks()
    override fun getNoteStream(id: Int): Flow<Note?> = noteDao.getNote(id)
    override suspend fun insertNote(item: Note): Long = noteDao.insert(item)
    override suspend fun deleteNote(item: Note) = noteDao.delete(item)
    override suspend fun updateNote(item: Note) = noteDao.update(item)

    // Recordatorios
    /*override fun getRemindersForNoteStream(noteId: Int): Flow<List<Reminder>> = noteDao.getRemindersForNote(noteId)
    override suspend fun insertReminder(item: Reminder): Long = noteDao.insertReminder(item)
    override suspend fun deleteReminder(item: Reminder) = noteDao.deleteReminder(item)

    override suspend fun deleteNotificationsByTaskId(taskId: Int) {
        noteDao.deleteNotificationsByTaskId(taskId)
    }*/

}
