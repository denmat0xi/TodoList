package com.hitsproject.todolist

import TodoItem
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class TodoAdapter(
    private val context: Context,
    private val todos: MutableList<TodoItem>,
    private val onDelete: (TodoItem) -> Unit,
    private val onEdit: (TodoItem, Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = todos.size

    override fun getItem(position: Int): Any = todos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false)

        val todo = todos[position]
        view.findViewById<TextView>(R.id.textViewDescription).text = todo.description
        view.findViewById<CheckBox>(R.id.checkBoxCompleted).apply {
            isChecked = todo.completed
            setOnCheckedChangeListener { _, isChecked ->
                todo.completed = isChecked
            }
        }

        view.findViewById<Button>(R.id.buttonEdit).setOnClickListener {
            showEditDialog(position, todo)
        }

        view.findViewById<Button>(R.id.buttonDelete).setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Удалить дело")
                .setMessage("Вы уверены, что хотите удалить это дело?")
                .setPositiveButton("Да") { _, _ ->
                    onDelete(todo)
                }
                .setNegativeButton("Нет", null)
                .show()
        }

        return view
    }

    private fun showEditDialog(position: Int, todo: TodoItem) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Изменить дело")

        val input = EditText(context)
        input.setText(todo.description)
        builder.setView(input)

        builder.setPositiveButton("Сохранить") { _, _ ->
            val newDescription = input.text.toString()
            todo.description = newDescription
            onEdit(todo, position)
        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}