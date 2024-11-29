package com.example.notesproyect.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.WorkManager
import com.example.notesproyect.StorageApplication
import com.example.notesproyect.viewmodel.NotesViewModel

@RequiresApi(Build.VERSION_CODES.O)
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val context = storageApplication()
            val workManager = WorkManager.getInstance(context)
            NotesViewModel(
                storageApplication().container.notesRepository,
                workManager
            )
        }
    }
}

fun CreationExtras.storageApplication(): StorageApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as StorageApplication)

