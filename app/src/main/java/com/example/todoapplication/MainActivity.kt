package com.example.todoapplication

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapplication.adapter.TodoAdapter
import com.example.todoapplication.SqliteDbHelper.DatabaseHelper
import com.example.todoapplication.datamodel.TodoItem
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var todoListView: ListView
    private lateinit var addTodoButton: Button
    private lateinit var selectDateButton: Button
    private lateinit var selectTimeButton: Button
    private lateinit var todoTitleEditText: EditText
    private lateinit var todoContentEditText: EditText
    private var selectedHourOfDay: Int = 0
    private var selectedMinute: Int = 0
    private var selectedDateMillis: Long = 0
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        todoListView = findViewById(R.id.todoListView)
        addTodoButton = findViewById(R.id.addTodoButton)
        selectDateButton = findViewById(R.id.selectDateButton)
        selectTimeButton = findViewById(R.id.selectTimeButton)
        todoTitleEditText = findViewById(R.id.todoTitleEditText)
        todoContentEditText = findViewById(R.id.todoContentEditText)


        dbHelper = DatabaseHelper(this)


        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


        todoAdapter = TodoAdapter(this, dbHelper.getAllTodoItems().toMutableList(),
            editClickListener = { position ->
                editTodoItem(todoAdapter.getItem(position))
            },
            deleteClickListener = { position ->
                deleteTodoItem(todoAdapter.getItem(position))
            })
        todoListView.adapter = todoAdapter


        addTodoButton.setOnClickListener {
            val title = todoTitleEditText.text.toString()
            val content = todoContentEditText.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty() && selectedDateMillis != 0L) {
                val hasReminder = true // Set a reminder for the newly created todo

                // Calculate selected time
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = selectedDateMillis
                    set(Calendar.HOUR_OF_DAY, selectedHourOfDay)
                    set(Calendar.MINUTE, selectedMinute)
                }
                val selectedTimeMillis = calendar.timeInMillis

                val id = dbHelper.insertTodoItem(title, content, selectedTimeMillis, hasReminder)
                if (id > 0) {
                    // Update the list view with the new data
                    todoAdapter.updateList(dbHelper.getAllTodoItems())
                    Toast.makeText(this, "Todo item added", Toast.LENGTH_SHORT).show()

                    // Set alarm for the newly created todo
                 //   setAlarmForTodoItem(id, title, content, selectedTimeMillis)
                } else {
                    Toast.makeText(this, "Failed to add todo item", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Please fill all fields and select date/time",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }





        // Set onClickListener for selectDateButton
        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Set onClickListener for selectTimeButton
        selectTimeButton.setOnClickListener {
            showTimePickerDialog()
        }
        todoAdapter.onDeleteListener = { position ->
            deleteTodoItem(todoAdapter.getItem(position))
        }
    }



    private fun deleteTodoItem(todoItem: TodoItem?) {
        if (todoItem != null) {
            val rowsAffected = dbHelper.deleteTodoItem(todoItem.id)
            if (rowsAffected > 0) {
                // Update the list view with the new data
                todoAdapter.updateList(dbHelper.getAllTodoItems())
                Toast.makeText(this, "Todo item deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to delete todo item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editTodoItem(todoItem: TodoItem?) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_todo, null)
        val dialogTitleEditText = dialogView.findViewById<EditText>(R.id.dialogTitleEditText)
        val dialogContentEditText = dialogView.findViewById<EditText>(R.id.dialogContentEditText)
        val selectDateButton = dialogView.findViewById<Button>(R.id.selectDateButton)
        val selectTimeButton = dialogView.findViewById<Button>(R.id.selectTimeButton)


        if (todoItem != null) {
            dialogTitleEditText.setText(todoItem.title)
            dialogContentEditText.setText(todoItem.content)
        }


        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit Todo Item")
            .setPositiveButton("Save") { dialog, which ->

                val updatedTitle = dialogTitleEditText.text.toString()
                val updatedContent = dialogContentEditText.text.toString()


                if (todoItem != null) {
                    val rowsAffected = dbHelper.updateTodoItem(
                        todoItem.id,
                        updatedTitle,
                        updatedContent,
                        selectedDateMillis,
                        todoItem.hasReminder
                    )
                    if (rowsAffected > 0) {
                        // Update the list view with the new data
                        val updatedTodoItem = TodoItem(
                            todoItem.id,
                            updatedTitle,
                            updatedContent,
                            selectedDateMillis,
                            todoItem.hasReminder
                        )
                        todoAdapter.updateItem(todoItem, updatedTodoItem)
                        Toast.makeText(this, "Todo item updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update todo item", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

        // Set onClickListener for selectDateButton
        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Set onClickListener for selectTimeButton
        selectTimeButton.setOnClickListener {
            showTimePickerDialog()
        }

        dialogBuilder.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(this, this, year, month, dayOfMonth)
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Show TimePickerDialog
        val timePickerDialog = TimePickerDialog(this, this, hourOfDay, minute, true)
        timePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        selectedDateMillis = calendar.timeInMillis

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val selectedDate = dateFormat.format(calendar.time)

        selectDateButton.text = selectedDate  // Update selectDateButton text
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val selectedTime = timeFormat.format(calendar.time)

        selectTimeButton.text = selectedTime  // Update selectTimeButton text
    }


    private fun setAlarmForTodoItem(id: Long, title: String, content: String, dateMillis: Long,selectedTime: String) {
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("todo_title", title)
            putExtra("todo_content", content)
            putExtra("todo_time", selectedTime)
            putExtra("todo_time", selectedTime)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            dateMillis,
            pendingIntent
        )
    }


}
