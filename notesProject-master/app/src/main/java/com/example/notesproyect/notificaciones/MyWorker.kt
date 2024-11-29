import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import com.example.notesproyect.MainActivity
import com.example.notesproyect.R
import com.example.notesproyect.notificaciones.notificacion
import java.util.UUID
import kotlin.random.Random

class MyWorker(
    private val context: Context,
    private val workerParameters : WorkerParameters
) : CoroutineWorker( context, workerParameters){

    override  suspend fun  doWork(): Result{
        showNotification()
        Log.d("remembers","im chambing bro")
        return Result.success()
    }


    private suspend fun showNotification() {
        val message = inputData.getString("EXTRA_MESSAGE") ?: "Evento por venir"
        val notificationId = inputData.getInt("NOTIFICATION_ID", 1)

        Log.d("remembers", "${notificationId}")
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)



        val notification = NotificationCompat.Builder(applicationContext, "alarm_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Evento Próximo")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId as Int, notification)
    }
}