package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
fun NotificationManager.sendNotification(fileName: String, status: String, context: Context) {
    val detailIntent = Intent(context, DetailActivity::class.java)
    detailIntent.putExtra("filename", fileName)
    detailIntent.putExtra("status", status)
    val detailPendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, detailIntent, PendingIntent.FLAG_ONE_SHOT)
    val builder = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(context.getString(R.string.notification_description))
        .addAction(0, context.getString(R.string.notification_action), detailPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancel() {
    cancelAll()
}