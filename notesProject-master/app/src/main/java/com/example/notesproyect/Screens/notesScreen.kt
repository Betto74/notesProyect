package com.example.notesproyect.Screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notesproyect.R
import com.example.notesproyect.navegacion.AppNavigation
import com.example.notesproyect.navegacion.AppScreen
import com.example.notesproyect.ui.AppViewModelProvider
import com.example.notesproyect.viewmodel.NotesViewModel
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.notesproyect.data.ComposeFileProvider
import com.example.notesproyect.data.AudioRecorder
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.notesproyect.viewmodel.TimeOption
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.MediaItem
import java.io.File
import java.security.KeyStore.TrustedCertificateEntry
import kotlin.math.log

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notas(
    navController: NavController,
    text: String,
    id: Int,
    viewModel: NotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val loaded = viewModel.isLoaded.collectAsState().value
    Log.d("calando", "loaded ${loaded}")
    if (id != -1  && loaded == false) {
        viewModel.fetchNoteById(id)
        val note = viewModel.currentNote.collectAsState().value
        Log.d("calando","${note}")
        if(note != null){
            viewModel.loadNoteAttachments(note)
        }
    }
    var uri: Uri? = null

    var hasImage by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var hasVideo by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val REQUEST_CODE_NOTIFICATIONS = 1001
    val REQUEST_CODE_AUDIO = 200
    val REQUEST_CODE_CAMERA = 100
// Lanzador para tomar fotos
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageUri?.let { uri ->
                    viewModel.imageUris.add(uri) // Agrega la URI directamente al ViewModel
                    Log.d("NotesScreen", "Imagen capturada añadida: $uri")
                }
                hasImage = success
            }
        }
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val copiedUri = copyUriToInternalStorage(context, it, "file_${System.currentTimeMillis()}")
                copiedUri?.let { finalUri ->
                    viewModel.fileUris.add(finalUri) // Agrega la URI del archivo copiado
                    Log.d("NotesScreen", "Archivo guardado como: $finalUri")
                    Log.d("NotesScreen", "EL TAO TAO: $uri")
                }
            }
        }
    )
    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success ->
            if (success) {
                imageUri?.let { uri -> // Verifica que `imageUri` tenga un valor válido
                    val copiedUri = copyVideoToInternalStorage(
                        context = context,
                        uri = uri,
                        fileName = "video_${System.currentTimeMillis()}.mp4"
                    )
                    copiedUri?.let { finalUri ->
                        viewModel.videoUris.add(finalUri) // Agrega la URI del archivo copiado al ViewModel
                        Log.d("NotesScreen", "Video capturado añadido y guardado como: $finalUri")
                    } ?: Log.e("NotesScreen", "Error copiando el video al almacenamiento interno.")
                } ?: Log.e("NotesScreen", "Error: URI de video es nulo")
                hasVideo = success
            }
        }
    )

    val titulo: String by viewModel.titulo.collectAsState()
    val descripcion: String by viewModel.descripcion.collectAsState()
    val isModalVisible: Boolean by viewModel.isModalVisible.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                if (text != null) {
                    Top(navController, text, viewModel)
                }
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
            ) {
                val labelT = stringResource(id = R.string.title)
                val labelD = stringResource(id = R.string.description)
                val labelAddFile = stringResource(id = R.string.addFile)
                val labelAddnotification = stringResource(id = R.string.addNoti)
                val archivosAdjuntos =
                    (viewModel.imageUris + viewModel.videoUris).joinToString(",") { it.toString() }
                viewModel.updateArchivos(archivosAdjuntos)

                InputField(
                    label = labelT,
                    value = titulo,
                    onValueChanged = { viewModel.updateTitle(it) })

                InputField(
                    label = labelD,
                    value = descripcion,
                    onValueChanged = { viewModel.updateDescription(it) })


                TextButton(onClick = {
                    imagePicker.launch("*/*")
                    viewModel.updateArchivos(archivosAdjuntos)

                }, modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
                    Text(labelAddFile, style = MaterialTheme.typography.titleMedium)
                }
                TextButton(onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Si no está concedido, solicitar el permiso
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CODE_CAMERA
                        )
                    } else {
                    uri = ComposeFileProvider.getImageUri(context)
                    imageUri = uri
                    cameraLauncher.launch(uri!!)
                    viewModel.updateArchivos(archivosAdjuntos)
                }
                }, modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
                    Text(stringResource(id = R.string.take_pic), style = MaterialTheme.typography.titleMedium)
                }



                TextButton(onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Si no está concedido, solicitar el permiso
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CODE_CAMERA
                        )
                    } else {
                    val uri = ComposeFileProvider.getImageUri(context)
                    imageUri = uri
                    videoLauncher.launch(uri)

                    viewModel.updateArchivos(archivosAdjuntos)
                }
                }, modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
                    Text(stringResource(id = R.string.take_vid), style = MaterialTheme.typography.titleMedium)
                }

                val isRecording = remember { mutableStateOf(false) }
                val recorder = remember { AudioRecorder(context) }
                Button(
                    onClick = {
                        // Verificar si el permiso de grabación de audio ya está concedido
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED) {
                            // Si no está concedido, se solicita el permiso
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf(Manifest.permission.RECORD_AUDIO),
                                REQUEST_CODE_AUDIO
                            )
                        } else {
                            // Si el permiso ya está concedido, inicia o detiene la grabación
                            if (isRecording.value) {
                                recorder.stopRecording()
                                viewModel.audioUris.add(recorder.currentFileUri)
                                Log.d("NotesScreen", "Audio capturado añadido y guardado como: $recorder.currentFileUri")
                            } else {
                                recorder.startRecording()
                            }
                            isRecording.value = !isRecording.value
                        }
                    },
                    modifier = Modifier.padding(8.dp)

                ) {
                    Text(if (isRecording.value) stringResource(id = R.string.stop_rec) else stringResource(id = R.string.start_rec))

                }


                MostrarMedia(viewModel = viewModel,true, text == TipoNota.TAREA.name )
                //MostrarDocumentos(viewModel = viewModel,true)

                if (text == TipoNota.TAREA.name) {

                    TextButton(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // Si no está concedido, solicitar el permiso
                                ActivityCompat.requestPermissions(
                                    context as Activity,
                                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                    REQUEST_CODE_NOTIFICATIONS
                                )
                            } else {
                                // Si el permiso ya está concedido, mostrar la notificación
                                viewModel.updateModalVisible(flag = true)
                            }},
                        modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                    ) {
                        Text(labelAddnotification, style = MaterialTheme.typography.titleMedium)
                    }

                    FechaHoraPicker(viewModel)
                }


            }
        }

        if( isModalVisible ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { }, // Cerrar al hacer clic fuera del modal
                contentAlignment = Alignment.Center
            ){
                agregarNotificacion(viewModel)

            }
        }
    }

}
fun copyVideoToInternalStorage(context: Context, uri: Uri, fileName: String): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, fileName) // Directorio interno
        val outputStream = file.outputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output) // Copiar el contenido del URI al archivo
            }
        }

        Log.d("FileCopy", "Video guardado en: ${file.absolutePath}")
        Uri.fromFile(file) // Devuelve la URI del archivo copiado
    } catch (e: Exception) {
        Log.e("FileCopy", "Error copiando archivo de video: $e")
        null
    }
}

fun copyUriToInternalStorage(context: Context, uri: Uri, fileName: String): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, fileName) // Guardamos en el directorio interno de la app
        val outputStream = file.outputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output) // Copiamos el contenido
            }
        }

        Log.d("FileCopy", "Archivo guardado en: ${file.absolutePath}")
        Uri.fromFile(file) // Retornamos la URI del archivo copiado
    } catch (e: Exception) {
        Log.e("FileCopy", "Error copiando archivo: $e")
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun agregarNotificacion(viewModel: NotesViewModel){

    val recordatorio: String by viewModel.recordatorio.collectAsState()

    Box(
        modifier = Modifier
            .padding(16.dp) // Espaciado entre el modal y los bordes de la pantalla
            .clip(RoundedCornerShape(16.dp)) // Esquinas redondeadas
            .background(MaterialTheme.colorScheme.primaryContainer) // Fondo del modal
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
//          stringResource(id = R.string.reminder)
            val labelAddnotification = stringResource(id = R.string.addNoti)
            Text(labelAddnotification)
            InputField(
                label = stringResource(id = R.string.reminder),
                value = recordatorio,
                onValueChanged = { viewModel.updateRecordatorio(it) })

            TimeOptionsCheckboxes(viewModel = viewModel)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(onClick = { viewModel.updateModalVisible(flag = false) }) {
                    Text(stringResource(id = R.string.cancel))
                }
                Button(onClick = { viewModel.addTimestamp() }) {
                    Text(stringResource(id = R.string.accept))
                }
            }

        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MostrarTiempos(viewModel: NotesViewModel, flag : Boolean) {
    val timestamps by viewModel.eventTimestamps.collectAsState()
    Log.d("remembers","${timestamps.size}")

    Column(modifier = Modifier.padding(16.dp)) {
        // Recorrer cada timestamp y convertirlo
        timestamps.forEach { timestamp ->
            val timeString = convertMillisToTimeString(timestamp, context = LocalContext.current)
            Row(
                modifier = Modifier
                    .fillMaxWidth(), // Añadir un poco de separación entre los elementos
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Mostrar el timestamp convertido
                Text(timeString, style = MaterialTheme.typography.bodyMedium)

                // Botón de borrar con icono
                if(  flag ){
                    IconButton(onClick = { viewModel.removeTimestamp(timestamp) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Borrar Timestamp",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeOptionsCheckboxes(viewModel: NotesViewModel) {

    val options = listOf(stringResource(id = R.string.minutes),stringResource(id = R.string.hours),
        stringResource(id = R.string.days), stringResource(id = R.string.weeks)) // Etiquetas de los CheckBoxes
    val selectedIndex by viewModel.selectedOption.collectAsState() // Observamos el índice seleccionado

    Column(modifier = Modifier.padding(16.dp)) {
        options.forEachIndexed { index, label ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedIndex == index,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            viewModel.updateOption(index) // Actualizamos el índice seleccionado
                        }
                    }
                )
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}





//MOSTRAR MULTIMEDIA
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MostrarMedia(viewModel: NotesViewModel,isEdit: Boolean, isTask : Boolean) {
    val context = LocalContext.current
    val recorder = remember { AudioRecorder(context) }


    var expandedImageUri by remember { mutableStateOf<Uri?>(null) }
    var expandedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var showFullscreenDialog by remember { mutableStateOf(false) }


    // Mostrar grabaciones existentes
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        itemsIndexed(viewModel.audioUris) { index, uri -> // Usa itemsIndexed para obtener el índice
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (uri.toString().isNotEmpty()) {
                    Text("Audio ${index + 1}", modifier = Modifier.weight(1f))
                    IconButton(onClick = { recorder.playAudio(uri) }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Reproducir"
                        )
                    }
                    if(isEdit){
                        IconButton(
                            onClick = { viewModel.removeAudio(uri) },

                            ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete image",
                                tint = Color.Red
                            )
                        }
                    }

                }
            }
        }



                //Muestra imagenes
        items(viewModel.imageUris) { uri ->
            Box(modifier = Modifier.padding(8.dp),contentAlignment = Alignment.Center) {
                if (uri.toString() != "") {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { expandedImageUri = uri }
                    )
                    if(isEdit) {
                        IconButton(
                            onClick = { viewModel.removeImage(uri) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete image",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
//muestra video
        items(viewModel.videoUris) { uri ->
            Row {
                if (uri.toString() != "") {
                    VideoPlayer(
                        videoUri = uri,
                        modifier = Modifier
                            .width(280.dp)
                            .height(200.dp)
                            .padding(8.dp)

                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if(isEdit) {
                            IconButton(
                                onClick = {
                                    viewModel.removeVideo(uri)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre los botones

                        IconButton(
                            onClick = {
                                expandedVideoUri = uri
                                showFullscreenDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "max",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
        }

        //Files


        items(viewModel.fileUris) { uri ->
            Row {
                // Ícono para representar el archivo
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "File",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Botón para abrir el archivo
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "/") // Permite abrir cualquier tipo de archivo
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Mostrar el selector de aplicaciones
                        context.startActivity(Intent.createChooser(intent, "Open file with"))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open file",
                        tint = Color.Blue
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Botón para eliminar el archivo
                IconButton(onClick = { viewModel.removeFile(uri) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
        item{
            if( isTask ) {
                MostrarTiempos(viewModel,flag = isEdit)
            }

        }

    }
    if (showFullscreenDialog && expandedVideoUri != null) {
        Dialog(
            onDismissRequest = { showFullscreenDialog = false }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                VideoPlayer(
                    videoUri = expandedVideoUri!!,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
                IconButton(
                    onClick = {
                        showFullscreenDialog = false // Cierra el diálogo
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }

    if (expandedImageUri != null) {
        Dialog(onDismissRequest = { expandedImageUri = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { expandedImageUri = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = expandedImageUri,
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

    }
}
fun openDocument(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf") // Cambia el tipo MIME según el tipo de archivo
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY
    }
    context.startActivity(intent)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MostrarDocumentos(viewModel: NotesViewModel, isEdit: Boolean) {
    val context = LocalContext.current
    var expandedDocumentUri by remember { mutableStateOf<Uri?>(null) }

    // Mostrar documentos existentesval context = LocalContext.current
    //        var expandedDocumentUri by remember { mutableStateOf<Uri?>(null) }
    LazyColumn(modifier = Modifier.padding(8.dp)) {

        items(viewModel.fileUris) { uri ->
            Row {
                // Ícono para representar el archivo
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "File",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Botón para abrir el archivo
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "/") // Permite abrir cualquier tipo de archivo
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Mostrar el selector de aplicaciones
                        context.startActivity(Intent.createChooser(intent, "Open file with"))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open file",
                        tint = Color.Blue
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Botón para eliminar el archivo
                IconButton(onClick = { viewModel.removeFile(uri) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }



}

@Composable
fun FileItem(uri: Uri, onDelete: () -> Unit) {
    val context = LocalContext.current

    Row {
        // Ícono para representar el archivo
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = "File",
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Botón para abrir el archivo
        IconButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "/") // Permite abrir cualquier tipo de archivo
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // Mostrar el selector de aplicaciones
                context.startActivity(Intent.createChooser(intent, "Open file with"))
            }
        ) {
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Open file",
                tint = Color.Blue
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Botón para eliminar el archivo
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
            }
        }
}

@Composable
fun VideoPlayer(videoUri: Uri, modifier: Modifier = Modifier.fillMaxWidth()) {
    val context = LocalContext.current
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }
    val playbackState = exoPlayer
    val isPlaying = playbackState?.isPlaying ?: false

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = modifier
    )


}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Top(navController: NavController, text: String, viewModel: NotesViewModel) {
    val labelNewTask = stringResource(id = R.string.newTask)
    val labelNewNote = stringResource(id = R.string.newNote)
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.cancel),
                    contentDescription = "Cancelar",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (text == TipoNota.NOTA.name) Text(
                    labelNewNote,
                    style = MaterialTheme.typography.titleMedium
                )
                else Text(labelNewTask, style = MaterialTheme.typography.titleMedium)

            }
        },
        actions = {
            val context = LocalContext.current
            IconButton(onClick = {

                viewModel.saveNote(text,context)
                navController.popBackStack()
            }) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = "Guardar",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaHoraPicker(viewModel: NotesViewModel) {

    val horas: Int by viewModel.horas.collectAsState()
    val minutos: Int by viewModel.minutos.collectAsState()
    val fecha: LocalDate by viewModel.fecha.collectAsState()

    val calendarState = rememberSheetState()
    CalendarDialog(
        state = calendarState,
        selection = CalendarSelection.Date { d ->

            viewModel.updateDate(d)
        }
    )


    val clockState = rememberSheetState()

    ClockDialog(
        state = clockState,
        config = ClockConfig(
            is24HourFormat = true
        ),
        selection = ClockSelection.HoursMinutes { h, m ->
            viewModel.updateTime(h, m)
        }
    )

    Row(modifier = Modifier.padding(start = 15.dp, top = 20.dp, end = 15.dp)) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val labelD = stringResource(id = R.string.date)
            Text(labelD, style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                calendarState.show()
            }) {
                Text(fecha.toString())
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val labelT = stringResource(id = R.string.time)
            Text(labelT, style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                clockState.show()
            }) {
                val formattedTime = String.format("%02d:%02d", horas, minutos)
                Text(formattedTime)
            }
        }
    }

}

@Composable
fun

    InputField(label: String, value: String, onValueChanged: (String) -> Unit) {
    Text(
        label,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(start = 15.dp, top = 20.dp, end = 15.dp)
            .fillMaxWidth()
    )
    textBox(
        label = label,
        value = value,
        onValueChanged = onValueChanged,
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .fillMaxWidth(),
        onValueChange = onValueChanged
    )
}


fun convertMillisToTimeString(timestamp: Long, context: Context): String {
    val min = context.getString(R.string.minutesbefore) // Accediendo a la cadena desde los recursos
    return when {
        timestamp == 0L -> {
            context.getString(R.string.atmoment) // Obtener el texto desde los recursos
        }
        timestamp % 604800000L == 0L -> {  // 1 semana = 604800000 milisegundos
            val weeks = timestamp / 604800000L
            "$weeks ${context.getString(R.string.weeksbefore)}" // Concatenar el texto adecuado
        }
        timestamp % 86400000L == 0L -> {  // 1 día = 86400000 milisegundos
            val days = timestamp / 86400000L
            "$days ${context.getString(R.string.daysbefore)}"
        }
        timestamp % 3600000L == 0L -> {  // 1 hora = 3600000 milisegundos
            val hours = timestamp / 3600000L
            "$hours ${context.getString(R.string.hoursbefore)}"
        }
        timestamp % 60000L == 0L -> {  // 1 minuto = 60000 milisegundos
            val minutes = timestamp / 60000L
            "$minutes $min"
        }
        else -> "$timestamp ${context.getString(R.string.minutesbefore)}"  // Menos de un minuto
    }
}

