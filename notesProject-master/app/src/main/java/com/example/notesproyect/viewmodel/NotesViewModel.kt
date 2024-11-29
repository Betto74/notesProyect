package com.example.notesproyect.viewmodel

import MyWorker
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesproyect.Screens.TipoNota
import com.example.notesproyect.data.Note
import com.example.notesproyect.data.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.log

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.single
import java.text.ParseException
import java.util.SortedSet
import java.util.concurrent.TimeUnit


data class NotesUiState(
    val notesList: List<Note> = emptyList(),
    val tasksList: List<Note> = emptyList()
)

data class TimeOption(val label: String, var isSelected: Boolean = false)

@RequiresApi(Build.VERSION_CODES.O)
class NotesViewModel(private val notesRepository: NotesRepository, private val workManager: WorkManager) : ViewModel() {

    /************ Estado UI ************/

    val noteUiState: StateFlow<NotesUiState> = combine(
        notesRepository.getAllNotesStream(),
        notesRepository.getAllTasksStream()
    ) { notes, tasks ->
        NotesUiState(notesList = notes, tasksList = tasks)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotesUiState()
    )



    /********** Edit **********/

    private val _isEditing = MutableStateFlow(false)

    private val _currentNote = MutableStateFlow<Note?>(null) // Nota actual
    val currentNote: StateFlow<Note?> get() = _currentNote

    private fun parseFecha(fechaString: String): LocalDateTime? {
        return try {
            // Supongamos que usaste este formato al guardar
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            LocalDateTime.parse(fechaString, formatter) // Parsear el String a LocalDateTime
        } catch (e: DateTimeParseException) {
            null // Retorna null si la fecha no se puede parsear
        }
    }

    fun fetchNoteById(noteId: Int) {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        viewModelScope.launch {
            if (noteId != -1) {
                Log.d("NotesViewModel", "Mi id es : $noteId")
                notesRepository.getNoteStream(noteId).collect { note ->
                    _currentNote.value = note
                    Log.d("calando","mi current note ${_currentNote.value}")
                    if (note != null) {
                        _titulo.value = note.titulo
                        _descripcion.value = note.descripcion
                        _isEditing.value = true

                        val localDateTime = LocalDateTime.parse(note.fechaVencimiento, formatter)
                        _fecha.value = localDateTime.toLocalDate()
                        _horas.value = localDateTime.toLocalTime().hour
                        _minutos.value = localDateTime.toLocalTime().minute

                        val sortedSet = note.recordatorioTimestamp.split(",")
                            .mapNotNull { it.trim().takeIf { it.isNotEmpty() }?.toLongOrNull() }
                            .toSortedSet()
                        _eventTimestamps.value = sortedSet

                        loadNoteAttachments(note)
                    }

                }
            } else {
                _currentNote.value = null
                _isEditing.value = false

            }


        }
        _isLoaded.value = true
    }

    fun updateNoteHecha(note: Note, hecha : Boolean,context: Context) {

            note?.let {
                Log.d("ViewModel", "Si se actualizo papi: $it")
                val nota = it.copy(hecha = hecha, recordatorioTimestamp = "")

                Log.d("ViewModel", "Si se actualizo papi: $nota")
                viewModelScope.launch {
                    notesRepository.updateNote(nota) // Llama a tu método de actualización
                    cancelWork(nota.id.toLong(),context)
                }


            }

    }

    /********** Delete **********/

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            if (noteId != -1) {
                // Obtenemos el flujo de la nota y recogemos su valor
                notesRepository.getNoteStream(noteId).collect { note ->
                    // Asegúrate de que la nota no sea null antes de intentar eliminar
                    note?.let {
                        notesRepository.deleteNote(it) // Pasar la nota al método de eliminación
                        Log.d("NotesViewModel", "Nota eliminada: $it")
                    } ?: run {
                        Log.d("NotesViewModel", "No se encontró la nota con id: $noteId")
                    }
                }
            } else {
                Log.d("NotesViewModel", "ID inválido para eliminar: $noteId")
            }
        }
    }


    /********** Pestañas de Navegación **********/

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> get() = _selectedTabIndex

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index

    }

    /********** Campos de Notas **********/

    private val _titulo = MutableStateFlow("")
    val titulo: StateFlow<String> get() = _titulo


    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> get() = _descripcion

    private val _fecha = MutableStateFlow(LocalDate.now())
    val fecha: StateFlow<LocalDate> get() = _fecha

    private val _horas = MutableStateFlow(currentHours())
    val horas: StateFlow<Int> get() = _horas

    private val _minutos = MutableStateFlow(currentMinutes())
    val minutos: StateFlow<Int> get() = _minutos

    private val _archivosAdjuntos = MutableStateFlow("")
    val archivosAdjuntos: StateFlow<String> get() = _archivosAdjuntos

    fun updateArchivos(newMedia: String){
        _archivosAdjuntos.value = newMedia
    }
    fun updateTitle(newTitle: String) {
        _titulo.value = newTitle
    }

    fun updateDescription(newDescription: String) {
        _descripcion.value = newDescription
    }

    fun updateDate(newDate: LocalDate) {
        _fecha.value = newDate
    }

    fun updateTime(newH: Int, newM: Int) {
        _horas.value = newH
        _minutos.value = newM
    }




    private val _audioUris = MutableStateFlow<List<Uri>>(emptyList())
    //val audioUris: StateFlow<List<Uri>> get() = _audioUris

    //AUDIO


    fun isImageUri(uri: Uri): Boolean {
        val imageExtensions = listOf(".png", ".jpg", ".jpeg")
        return imageExtensions.any { extension -> uri.toString().lowercase().endsWith(extension) } || uri.toString().lowercase().contains("/image%")
    }
    fun isVideoUri(uri: Uri): Boolean {
        val videoExtensions = listOf(".mp4")
        return videoExtensions.any { extension -> uri.toString().lowercase().endsWith(extension) } || uri.toString().lowercase().contains("/video%")
    }
    fun isAudio(uri: Uri): Boolean {
        val audioExtensions = listOf(".mp3", ".wav", ".aac", ".flac", ".ogg", ".m4a", ".wma")
        return audioExtensions.any { extension -> uri.toString().lowercase().endsWith(extension) } || uri.toString().lowercase().contains("/audio%")
    }
    val imageUris =  mutableStateListOf<Uri>()
    val videoUris =  mutableStateListOf<Uri>()
    val audioUris =  mutableStateListOf<Uri>()
    val fileUris =  mutableStateListOf<Uri>()


    fun loadNoteAttachments(note: Note) {
        Log.d("NotesViewModel", "Contenido de archivosAdjuntos: ${note.archivosAdjuntos}")
        val uris = note.archivosAdjuntos.split(",").map { Uri.parse(it) }
        imageUris.clear()
        videoUris.clear()
        fileUris.clear()
        audioUris.clear()
        uris.forEach {
            if (isImageUri(it)) {
                imageUris.add(it)
                Log.d("NotesViewModel", "Imagen cargada: $it")

            }
            else if (isVideoUri(it)){
                videoUris.add(it)
                Log.d("NotesViewModel", "Video cargado: $it")
            }
            else if (isAudio(it)){
                audioUris.add(it)
                Log.d("NotesViewModel", "Audio cargado: $it")
            }

            else {
                fileUris.add(it)
                Log.d("NotesViewModel", "Archivo cargado: $it")

            }
        }
        Log.d("NotesViewModel", "Imágenes cargadas: $imageUris")
        Log.d("NotesViewModel", "Videos cargados: $videoUris")
        Log.d("NotesViewModel", "Audios cargados: $audioUris")
    }

    fun removeImage(uri: Uri) {
        imageUris.remove(uri)
    }
    fun removeFile(uri: Uri) {
        fileUris.remove(uri)
    }

    fun removeVideo(uri: Uri) {
        videoUris.remove(uri)

    }

    fun removeAudio(uri: Uri) {
        audioUris.remove(uri)

    }


    /********** Funciones de Guardado **********/
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val _generatedId = MutableStateFlow<Long>(-1) // Inicializamos en -1
    val generatedId: StateFlow<Long> get() = _generatedId

    private val _generatedIdReminder = MutableStateFlow<Long>(-1) // Inicializamos en -1
    val generatedIdReminder: StateFlow<Long> get() = _generatedIdReminder

    fun saveNote(tipo: String, context: Context) {
        Log.d("NotesViewModel", "Imágenes a guardar: ${imageUris.joinToString()}")
        Log.d("NotesViewModel", "Videos a guardar: ${videoUris.joinToString()}")
        Log.d("NotesViewModel", "Audios a guardar: ${audioUris.joinToString()}")

        val archivosAdjuntos = (imageUris + videoUris + audioUris + fileUris).joinToString(",") { it.toString() }
        Log.d("NotesViewModel", "Archivos adjuntos: $archivosAdjuntos")
        val fechaCreacion = dateFormat.format(Date()) // Convertimos la fecha actual a String

        // Convertimos `LocalDateTime` a `Date` para formatear correctamente `fechaVencimiento`
        val fechaVencimiento = _fecha.value?.let {
            val vencimientoDate =
                Date.from(fechaConHora().atZone(ZoneId.systemDefault()).toInstant())
            dateFormat.format(vencimientoDate) // Formateamos como String
        }

        val nuevaNota = Note(
            id = if (_isEditing.value) _currentNote.value?.id ?: 0 else 0,
            tipo = if (_isEditing.value) _currentNote.value?.tipo ?: "NOTA" else tipo,
            hecha = if (_isEditing.value) _currentNote.value?.hecha ?: false else false,
            titulo = _titulo.value,
            descripcion = _descripcion.value,
            fechaCreacion = fechaCreacion,
            fechaVencimiento = fechaVencimiento,
            recordatorioTimestamp = _eventTimestamps.value.joinToString(separator = ","),
            archivosAdjuntos = archivosAdjuntos
        )

        viewModelScope.launch {
            if (_isEditing.value) {
                // Lógica para actualizar la nota existente

                Log.d("NotesViewModel", "Estoy actualizando : $nuevaNota")
                notesRepository.updateNote(nuevaNota) // Asegúrate de tener este método en tu repositorio
                _generatedId.value = nuevaNota.id.toLong()
            } else {
                // Lógica para insertar una nueva nota
                Log.d("NotesViewModel", "Estoy agregando : $nuevaNota")
                _generatedId.value = notesRepository.insertNote(nuevaNota) // Insertamos la nota en la base de datos
            }
            Log.d("remembers","${_generatedId.value} , size de mi remembers : ${_eventTimestamps.value.size}")

            crearRecordatorios(nuevaNota.titulo, nuevaNota.fechaVencimiento ?: "", context)

        }

        // Limpiamos los campos después de guardar
        _titulo.value = ""
        _descripcion.value = ""
        _isEditing.value = false // Reseteamos el estado
        _generatedId.value   = -1
    }
    private suspend fun crearRecordatorios(titulo : String,fechaVencimiento: String, context : Context ){
        cancelWork(generatedId.value,context)
        for (timestamp in _eventTimestamps.value){
            val mensaje = "Recordatorio: ${titulo} está por vencer en ${convertMillisToTimeString(timestamp)}"
            Log.d("remembers",mensaje)

            scheduleWorkWithOffset(fechaVencimiento ?: "", timestamp, mensaje, _generatedId.value, context)
        }
    }

    private fun fechaConHora(): LocalDateTime {
        return LocalDateTime.of(_fecha.value, LocalTime.of(_horas.value, _minutos.value))
    }

    private fun currentHours() = LocalDateTime.now().hour
    private fun currentMinutes() = LocalDateTime.now().minute

    /************ Pantalla principal: búsqueda y filtro ************/

    private val _buscar = MutableStateFlow("")
    val buscar: StateFlow<String> get() = _buscar

    fun onSearchChange(search: String) {
        _buscar.value = search

    }

    val listaFiltradas: StateFlow<List<Note>> = combine(
        noteUiState,
        _selectedTabIndex,
        _buscar
    ) { uiState, selectedTab, search ->

        val listaVisible = if (selectedTab == 0) uiState.notesList else uiState.tasksList


        if (search.isBlank()) listaVisible else {
            listaVisible.filter {
                it.titulo.contains(search, ignoreCase = true) ||
                        it.descripcion.contains(search, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    /********************** NOTIFICACIONES ********************/

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> get() = _isLoaded

    fun updateLoad(flag : Boolean){
        _isLoaded.value = flag
    }
    //val timeOptionsState : List<TimeOption>
    private val _isModalVisible = MutableStateFlow(false    )
    val isModalVisible: StateFlow<Boolean> get() = _isModalVisible

    fun updateModalVisible(flag: Boolean) {
        _isModalVisible.value = flag
        _selectedOption.value = -1
        _recordatorio.value = ""
    }


    private val _selectedOption = MutableStateFlow(-1)
    val selectedOption: StateFlow<Int> get() = _selectedOption

    fun updateOption(index: Int) {
        _selectedOption.value = index
    }

    private val _eventTimestamps = MutableStateFlow<SortedSet<Long>>(sortedSetOf())
    val eventTimestamps: StateFlow<SortedSet<Long>> get() = _eventTimestamps


    fun addTimestamp() {
        val selectedOption = _selectedOption.value
        val value = _recordatorio.value
        val timeInMillis: Long = when (selectedOption) {
            0 -> TimeUnit.MINUTES.toMillis(value.toLong()) // Minutos
            1 -> TimeUnit.HOURS.toMillis(value.toLong()) // Horas
            2 -> TimeUnit.DAYS.toMillis(value.toLong()) // Días
            3 -> TimeUnit.DAYS.toMillis(value.toLong() * 7) // Semanas (7 días)
            else -> 0L
        }
        val currentTimestamps = _eventTimestamps.value
        val updatedTimestamps = (currentTimestamps + timeInMillis).toSortedSet()

        _eventTimestamps.value = updatedTimestamps
        _recordatorio.value = ""
        _selectedOption.value = -1
        _isModalVisible.value = false
    }

    fun removeTimestamp(timestamp: Long) {
        val currentTimestamps = _eventTimestamps.value
        val updatedTimestamps = (currentTimestamps - timestamp).toSortedSet()

        _eventTimestamps.value = updatedTimestamps
    }

    private val _recordatorio = MutableStateFlow("")
    val recordatorio: StateFlow<String> get() = _recordatorio

    fun updateRecordatorio(newRecordatorio: String) {
        if ( newRecordatorio.all { it.isDigit() }) {
            _recordatorio.value = newRecordatorio
        }
    }



    fun scheduleWorkWithOffset(dateString: String, notifyBefore: Long, mensaje : String, taskId: Long, context: Context) {
        // Convertir la fecha de vencimiento a milisegundos
        val timestamp = convertToMillis(dateString)

        // Calcular el retraso en milisegundos (notificación antes de la fecha de vencimiento)
        val delayMillis = timestamp - System.currentTimeMillis() - notifyBefore
        Log.d("remembers","tiempo de la fecha ${timestamp} , tiempo general ${delayMillis}, mi id ${taskId}")
        if (delayMillis > 0) {
            // Crear el trabajo
            val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("EXTRA_MESSAGE" to mensaje, "NOTIFICATION_ID" to taskId))
                .addTag("task_${taskId}") // Etiqueta personalizada
                .build()

            // Encolar el trabajo en WorkManager

            Log.e("remembers", "Encolando ando")
            WorkManager.getInstance(context).enqueue(workRequest)
            Log.e("remembers", "Se encolo bien pa")

        } else {
            Log.e("remembers", "El tiempo para la notificación ya ha pasado.")
        }
    }

    fun cancelWork(taskId: Long, context: Context) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("task_${taskId}")
    }


    fun convertToMillis(dateString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        return date?.time ?: 0L
    }



    /********************** NOTIFICACIONES ********************/
}

private fun convertMillisToTimeString(timestamp: Long): String {
    return when {
        timestamp % 604800000L == 0L -> {  // 1 semana = 604800000 milisegundos
            val weeks = timestamp / 604800000L
            "$weeks semana(s)"
        }
        timestamp % 86400000L == 0L -> {  // 1 día = 86400000 milisegundos
            val days = timestamp / 86400000L
            "$days día(s)"
        }
        timestamp % 3600000L == 0L -> {  // 1 hora = 3600000 milisegundos
            val hours = timestamp / 3600000L
            "$hours hora(s)"
        }
        timestamp % 60000L == 0L -> {  // 1 minuto = 60000 milisegundos
            val minutes = timestamp / 60000L
            "$minutes minuto(s)"
        }
        else -> "$timestamp milisegundos"  // Menos de un minuto
    }
}

