package com.hitsproject.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import TodoItem

class MainActivity : AppCompatActivity() {

    private lateinit var todoList: MutableList<TodoItem>
    private lateinit var adapter: TodoAdapter
    private lateinit var listView: ListView
    private lateinit var inputField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)
        inputField = findViewById(R.id.inputField)
        todoList = JsonUtils.loadTodos(this).toMutableList()

        adapter = TodoAdapter(this, todoList, ::deleteTodo) { todo, position ->
            editTodo(todo, position)
        }
        listView.adapter = adapter

        findViewById<Button>(R.id.addButton).setOnClickListener {
            addTodo()
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveJson()
        }

        findViewById<Button>(R.id.loadButton).setOnClickListener {
            loadJson()
        }
    }

    private fun addTodo() {
        val description = inputField.text.toString()
        if (description.isNotEmpty()) {
            val newTodo = TodoItem(id = System.currentTimeMillis().toString(), description = description, completed = false)
            todoList.add(newTodo)
            adapter.notifyDataSetChanged()
            inputField.text.clear()
            JsonUtils.saveTodos(this, todoList)
        }
    }

    private fun editTodo(todo: TodoItem, position: Int) {
        todoList[position] = todo
        adapter.notifyDataSetChanged()
        JsonUtils.saveTodos(this, todoList)
    }

    private fun deleteTodo(todo: TodoItem) {
        val position = todoList.indexOf(todo)
        if (position != -1) {
            todoList.removeAt(position)
            adapter.notifyDataSetChanged()
            JsonUtils.saveTodos(this, todoList)
        }
    }

    private fun saveJson() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "todos.json")
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }

    private fun loadJson() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        startActivityForResult(intent, LOAD_FILE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                JsonUtils.saveTodosToUri(this, todoList, uri)
            }
        } else if (requestCode == LOAD_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                JsonUtils.loadTodosFromUri(this, uri)?.let { loadedTodos ->
                    todoList.clear()
                    todoList.addAll(loadedTodos)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        private const val CREATE_FILE_REQUEST_CODE = 1
        private const val LOAD_FILE_REQUEST_CODE = 2
    }
}