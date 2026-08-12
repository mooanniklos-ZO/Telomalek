package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class TelegramScraperService : Service() {

    companion object {
        const val CHANNEL_ID = "telejob_service_channel"
        const val CHANNEL_NAME = "خدمة رصد وظائف تلغرام"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "ACTION_START_SCRAPER"
        const val ACTION_STOP = "ACTION_STOP_SCRAPER"

        fun startService(context: Context) {
            val intent = Intent(context, TelegramScraperService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, TelegramScraperService::class.java).apply {
                action = ACTION_STOP
            }
            context.stopService(intent)
        }

        fun showJobNotification(context: Context, title: String, budget: String, sourceGroup: String) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "telejob_offers_channel",
                    "إشعارات الوظائف المستخرجة",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "تنبيهات عند العثور على فرصة عمل جديدة في القنوات"
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, "telejob_offers_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("💼 وظيفة جديدة: $title")
                .setContentText("الميزانية: $budget | المصدر: $sourceGroup")
                .setStyle(NotificationCompat.BigTextStyle().bigText("الميزانية المقترحة: $budget\nالقناة: $sourceGroup\nتم الفحص والتحليل بالذكاء الاصطناعي Gemini"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        }
    }

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = createForegroundNotification("الخدمة قيد التشغيل 24/7 لرصد القنوات المحددة...")
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TeleJobAI::ScraperServiceWakeLock").apply {
            acquire(24 * 60 * 60 * 1000L) // 24 hours max
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "رصد وتفريغ رسائل تلغرام في الخلفية بدون انقطاع"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification(statusText: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("تلي جوب AI - رصد الوظائف متصل")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
