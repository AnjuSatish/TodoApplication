package com.example.todoapplication.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.todoapplication.R
import com.example.todoapplication.datamodel.TodoItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodoAdapter(
    context: Context,
    private var todoItems: MutableList<TodoItem>,
    private val editClickListener: (Int) -> Unit,private val deleteClickListener: (Int) -> Unit
) :
    ArrayAdapter<TodoItem>(context, 0, todoItems) {
    var onDeleteListener: ((Int) -> Unit)? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false)
        }

        val titleTextView = view?.findViewById<TextView>(R.id.titleTextView)
        val contentTextView = view?.findViewById<TextView>(R.id.contentTextView)
        val dateTextView = view?.findViewById<TextView>(R.id.dateTextView) // Find dateTextView here
        val edit = view?.findViewById<ImageView>(R.id.editIcon)
        val deleteIcon = view?.findViewById<ImageView>(R.id.deleteIcon)
        val reminderCheckbox = view?.findViewById<CheckBox>(R.id.reminderCheckbox)
        val todoItem = todoItems[position]
        titleTextView?.text = todoItem.title
        contentTextView?.text = todoItem.content

        val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(todoItem.date))
        dateTextView?.text = formattedDate // Set dateTextView text with the formatted date and time

        edit?.setOnClickListener {
            editClickListener.invoke(position)
        }
        reminderCheckbox?.isChecked = todoItem.hasReminder


        reminderCheckbox?.setOnCheckedChangeListener { _, isChecked ->

            todoItem.hasReminder = isChecked

        }
        deleteIcon?.setOnClickListener {

            deleteClickListener.invoke(position)
        }

        return view!!
    }


    fun updateList(newList: List<TodoItem>) {
        todoItems.clear()
        todoItems.addAll(newList)
        notifyDataSetChanged()
    }
    private fun removeTodoItem(position: Int) {
        if (position in 0 until todoItems.size) {
            todoItems.removeAt(position)
            notifyDataSetChanged()
            onDeleteListener?.invoke(position)
        }
    }
    fun updateItem(oldItem: TodoItem, newItem: TodoItem) {
        val index = todoItems.indexOf(oldItem)
        if (index != -1) {
            todoItems[index] = newItem
            notifyDataSetChanged()
        }
    }

}
