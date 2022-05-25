package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.cancel()
        button = findViewById(R.id.button)
        val fileName = intent.extras?.getString("filename")
        val status = intent.extras?.getString("status")
        val filenameTxt: TextView = findViewById(R.id.filename)
        val statusTxt: TextView = findViewById(R.id.status)
        statusTxt.text = status
        filenameTxt.text = fileName

        button.setOnClickListener {
            finish()
        }
    }
}
