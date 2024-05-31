package com.example.todoapplication.datamodel
data class TodoItem(val id: Int, val title: String, val content: String, val date: Long, var hasReminder: Boolean )
