package com.example.notesproyect.notificaciones

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.notesproyect.MainActivity
import com.example.notesproyect.R

class notificacion : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("ALARMAACTIVA", "Se activo")
        var intentA = Intent(context, MainActivity::class.java)
        intentA.putExtra("action", 1)
        intentA.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val pI =
            PendingIntent.getActivity(context,  0,  intentA, PendingIntent.FLAG_IMMUTABLE)
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        val channelId = "alarm_id"
        context?.let { ctx ->
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm Demo")
                .setContentText("Notification sent with message $message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pI)
            notificationManager.notify(1, builder.build())
        }

    }
}