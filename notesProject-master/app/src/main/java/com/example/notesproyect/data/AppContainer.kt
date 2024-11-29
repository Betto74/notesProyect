package com.example.notesproyect.data

import android.content.Context

interface AppContainer {
    val notesRepository: NotesRepository
}

class AppDataContainer(private val context: Context)  : AppContainer{
    override val notesRepository: NotesRepository by lazy {
        OfflineNotesRepository(StorageDatabase.getDatabase(context).noteDao())
    }

}
