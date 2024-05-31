package com.example.todoapplication.SqliteDbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todoapplication.datamodel.TodoItem

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TodoDatabase"
        private const val TABLE_NAME = "TodoItems"
        private const val COLUMN_ID = "Id"
        private const val COLUMN_TITLE = "Title"
        private const val COLUMN_CONTENT = "Content"
        private const val COLUMN_DATE = "Date"
        private const val COLUMN_HAS_REMINDER = "HasReminder"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_CONTENT TEXT, " +
                "$COLUMN_DATE INTEGER, " +
                "$COLUMN_HAS_REMINDER INTEGER)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTodoItem(title: String, content: String, date: Long, hasReminder: Boolean): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE, title)
        contentValues.put(COLUMN_CONTENT, content)
        contentValues.put(COLUMN_DATE, date)
        contentValues.put(COLUMN_HAS_REMINDER, if (hasReminder) 1 else 0)
        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun updateTodoItem(id: Int, title: String, content: String, date: Long, hasReminder: Boolean): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE, title)
        contentValues.put(COLUMN_CONTENT, content)
        contentValues.put(COLUMN_DATE, date)
        contentValues.put(COLUMN_HAS_REMINDER, if (hasReminder) 1 else 0)
        return db.update(TABLE_NAME, contentValues, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun getAllTodoItems(): List<TodoItem> {
        val todoItemList = mutableListOf<TodoItem>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                val date = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE))
                val hasReminder = cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_REMINDER)) == 1
                todoItemList.add(TodoItem(id, title, content, date, hasReminder))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return todoItemList
    }

    fun deleteTodoItem(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }
}
