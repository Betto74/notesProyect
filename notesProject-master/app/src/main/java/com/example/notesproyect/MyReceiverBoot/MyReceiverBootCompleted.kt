package com.example.notesproyect.MyReceiverBoot


import MyWorker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.notesproyect.StorageApplication
import com.example.notesproyect.data.Note
import com.example.notesproyect.data.NotesRepository
import com.example.notesproyect.data.StorageDatabase
import com.example.notesproyect.ui.storageApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MyReceiverBootCompleted : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        //TODO("MyReceiverBootCompleted.onReceive() is not implemented")
        Log.d("CARGADO", "sE CARGO EL SISTEMA. AQUI SE DEBERAIN REPROGRMAR ALARMAS")
        Log.d("CARGADO",
            "El action del Intent: " + intent.action.toString())

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val notesRepository = (context.applicationContext as StorageApplication).container.notesRepository

            notesRepository.getAllTasksStream().collect { tasks ->
                val currentDate = System.currentTimeMillis()

                tasks.forEach { task ->
                    //sif (task.fechaVencimiento > currentDate) {
                        // Reprogramar alarma para la tarea usando WorkManager
                    reprogramarRecordatorios(task, context)
                  //  }
                }
            }
            //  val notesRepository = NotesRepository(noteDao)
        }
    }

    private suspend fun reprogramarRecordatorios(task: Note, context: Context) {
        val eventTimestamps = parseRecordatorioTimestamps(task.recordatorioTimestamp)
        val titulo = task.titulo
        val fechaVencimiento = task.fechaVencimiento.toString()

        for (timestamp in eventTimestamps) {
            val mensaje = "Recordatorio: $titulo está por vencer en ${convertMillisToTimeString(timestamp)}"
            scheduleWorkWithOffset(fechaVencimiento, timestamp, mensaje, task.id.toLong(), context)
        }
    }
    private fun scheduleWorkWithOffset(dateString: String, notifyBefore: Long, mensaje: String, taskId: Long, context: Context) {
        val timestamp = convertToMillis(dateString)
        val delayMillis = timestamp - System.currentTimeMillis() - notifyBefore

        if (delayMillis > 0) {
            val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("EXTRA_MESSAGE" to mensaje, "NOTIFICATION_ID" to taskId))
                .addTag("task_$taskId")
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
            Log.d("WORK_MANAGER", "Encolado para la tarea $taskId con mensaje: $mensaje")
        } else {
            Log.d("WORK_MANAGER", "El tiempo para la notificación ya ha pasado.")
        }
    }

    private fun parseRecordatorioTimestamps(timestamps: String): List<Long> {
        return if (timestamps.isNotEmpty()) {
            timestamps.split(",").mapNotNull { it.trim().toLongOrNull() }
        } else {
            emptyList()
        }
    }

    private fun convertMillisToTimeString(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        return "$minutes minutos"
    }

    private fun convertToMillis(dateString: String): Long {
        // Implementa la lógica para convertir un String a un timestamp en milisegundos
        // Ejemplo simple para ilustrar
        return System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1) // Remplaza con tu lógica real
    }
}