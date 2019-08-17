package com.example.splitpayandroid.messaging_service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.splitpayandroid.R
import com.example.splitpayandroid.groups.GroupsActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class MessagingService: FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        println("Generated token: $token")
    }

    private fun msgToJSON(msg: RemoteMessage): JSONObject{
        val res = JSONObject()
        res.put("title", msg.notification?.title ?: msg.data["title"])
        res.put("message", msg.notification?.body ?: msg.data["message"])
        return res
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = msgToJSON(message)
        val intent = Intent(this, GroupsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "SplitPay Groups")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle( data.getString("title"))
            .setSound(defaultSoundUri)
            .setContentText(data.getString("message"))
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build())
    }

}