package com.example.todoapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val title = intent?.getStringExtra("todo_title")
        val content = intent?.getStringExtra("todo_content")


        if (context != null && title != null && content != null) {
            showNotification(context, title, content)
        }
    }

    private fun showNotification(context: Context, title: String, content: String) {
        val message = "Alarm Received:\nTitle: $title\nContent: $content"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

