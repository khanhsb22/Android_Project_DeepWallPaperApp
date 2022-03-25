package khanhle.imageapp.repository

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.*
import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.annotation.RequiresApi
import khanhle.imageapp.R
import khanhle.imageapp.model.ImageInAlbum
import khanhle.imageapp.view.activity.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class ChangeWallPaperService : Service() {

    companion object {
        private val ONGOING_NOTIFICATION_ID = 222
    }

    private val timer = Timer()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Create a notification for the Foreground service to execute a repetitive task
        // The main purpose is for the service to still work when the app is off

        // Android 8 later require channel, Android < 7 nor require channel
        val notificationIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val channelId =
            if (SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("Deep_wallpaper_service", "Deep Wallpaper Service") // Se xuat hien khi user keo qua trai, phai
                // notification
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notification: Notification.Builder =
            if (SDK_INT > 25)
                Notification.Builder(this, channelId)
            else
                Notification.Builder(this)

        notification.setContentTitle("Service")
            .setContentText("Auto change wallpaper is running.")
            .setSmallIcon(R.drawable.icon_splash_screen)
            .setContentIntent(pendingIntent)
            .setTicker("Sticker for Deep Wallpaper app")

        startForeground(ONGOING_NOTIFICATION_ID, notification.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        stopForeground(true)
        Toast.makeText(this, "Auto change wallpaper has turned off.", Toast.LENGTH_SHORT).show()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Get from DialogAutoChangeWallPaper
        var images: ArrayList<ImageInAlbum> =
            intent!!.getSerializableExtra("images") as ArrayList<ImageInAlbum>
        var time = intent.getStringExtra("time")

        changeImageAfterTime(images, time!!)
        return START_STICKY
    }

    private fun changeImageAfterTime(images: ArrayList<ImageInAlbum>, time: String) {
        var count = -1
        var timeToLong: Long = -1

        when (time) {
            "1 minute" -> {
                //timeToLong = 5000
                timeToLong = 60000
            }
            "5 minutes" -> {
                timeToLong = 300000
            }
            "30 minutes" -> {
                timeToLong = 1800000
            }
            "1 hour" -> {
                timeToLong = 3600000
            }
            "3 hours" -> {
                timeToLong = 10800000
            }
            "1 day" -> {
                timeToLong = 86400000
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            val timerTask = object : TimerTask() {
                override fun run() {
                    count++

                    if (count == images.size) {
                        count = 0
                    }

                    var wallpaperManager = WallpaperManager.getInstance(this@ChangeWallPaperService)


                    try {
                        val url = URL(images[count].imageUrl)
                        val connection: HttpURLConnection =
                            url.openConnection() as HttpURLConnection
                        connection.setDoInput(true)
                        connection.connect()
                        val input: InputStream = connection.getInputStream()
                        val bitmap = BitmapFactory.decodeStream(input)
                        wallpaperManager.setBitmap(bitmap)

                    } catch (e: IOException) {
                    }

                }
            }
            timer.schedule(timerTask, 500, timeToLong) // Schedule(timerTask, delay, period)
        }
    }
}