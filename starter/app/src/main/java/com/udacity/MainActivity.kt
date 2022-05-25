package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var radioGroup: RadioGroup
    private lateinit var URL: String
    private lateinit var downloadedFile: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        radioGroup = findViewById(R.id.radioGroup)

        createChannel(getString(R.string.channel_id), getString(R.string.channel_name))
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.glide -> {
                    URL = URL_Glide
                    downloadedFile = (findViewById(checkedId) as RadioButton).text.toString()
                }
                R.id.load_app -> {
                    URL = URL_LoadingApp
                    downloadedFile = (findViewById(checkedId) as RadioButton).text.toString()
                }
                R.id.retrofit -> {
                    URL = URL_Retrofit
                    downloadedFile = (findViewById(checkedId) as RadioButton).text.toString()
                }
            }
        }
        custom_button.setOnClickListener {
            if (radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(applicationContext, "Please select the file to download", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (custom_button.buttonState == ButtonState.Loading) {
                return@setOnClickListener
            }
            custom_button.buttonState = ButtonState.Clicked
            download()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String, channelName: String) {
        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                custom_button.buttonState = ButtonState.Completed
                Toast.makeText(applicationContext, "Download is done", Toast.LENGTH_SHORT).show()
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.sendNotification(downloadedFile, queryDownloadStatus(downloadID), applicationContext)
            }
        }
    }

    private fun queryDownloadStatus(downloadId: Long) : String{
        val downloadManager: DownloadManager = applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        cursor.moveToFirst()
        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        when (status) {
            DownloadManager.STATUS_RUNNING -> return "Downloading"
            DownloadManager.STATUS_SUCCESSFUL -> return "Successful"
            DownloadManager.STATUS_FAILED -> return "Failed"
            else -> return "TBD"
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL_LoadingApp =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_Glide = "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_Retrofit = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
