package com.example.notesproyect.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: String,
    val titulo: String,
    val descripcion: String,
    val fechaCreacion: String,
    val archivosAdjuntos: String = "",
    val hecha : Boolean? = null,
    val fechaVencimiento: String? = null,
    val recordatorioTimestamp: String = "", // Guardar la fecha del recordatorio como timestamp
)


