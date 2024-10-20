package com.example.notesproyect.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TipoNota {
    NOTA,
    TAREA
}


data class Nota(
    /** Tipo de nota (nota o tarea) **/
    val tipo: TipoNota,
    /** Título de la nota o tarea **/
    val titulo: String,
    /** Descripción de la nota o tarea **/
    val descripcion: String,
    /** Fecha de creación **/
    val fechaCreacion: Date,
    /** Archivos asociados (imágenes, audios, videos, etc.) **/
    val archivosAdjuntos: List<Archivo> = emptyList(),
    /** Fecha de vencimiento (solo para tareas) **/
    val fechaVencimiento: Date? = null,
    /** Hora de vencimiento (solo para tareas) **/
    val horaVencimiento: Date? = null,
    /** Recordatorio en formato de tiempo (solo para tareas) **/
    val recordatorio: String? = null
)


data class Archivo(
    /** URL o ruta del archivo **/
    val uri: String,
    /** Tipo de archivo (imagen, audio, video, etc.) **/
    val tipo: String,
    /** Nombre del archivo **/
    val nombre: String,
    /** Tamaño del archivo en bytes **/
    val tamano: Long
)