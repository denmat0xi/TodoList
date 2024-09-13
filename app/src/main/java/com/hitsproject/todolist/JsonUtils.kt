package com.hitsproject.todolist

import TodoItem
import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object JsonUtils {
    private const val FILE_NAME = "todos.json"

    fun saveTodos(context: Context, todos: List<TodoItem>) {
        val jsonArray = JSONArray()
        todos.forEach { todo ->
            val jsonObject = JSONObject()
            jsonObject.put("id", todo.id)
            jsonObject.put("description", todo.description)
            jsonObject.put("completed", todo.completed)
            jsonArray.put(jsonObject)
        }
        val json = jsonArray.toString()
        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    fun loadTodos(context: Context): List<TodoItem> {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) {
                context.openFileInput(FILE_NAME).bufferedReader().use {
                    val json = it.readText()
                    val jsonArray = JSONArray(json)
                    val todos = mutableListOf<TodoItem>()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getString("id")
                        val description = jsonObject.getString("description")
                        val completed = jsonObject.getBoolean("completed")
                        todos.add(TodoItem(id, description, completed))
                    }
                    todos
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveTodosToUri(context: Context, todos: List<TodoItem>, uri: Uri) {
        val jsonArray = JSONArray()
        todos.forEach { todo ->
            val jsonObject = JSONObject()
            jsonObject.put("id", todo.id)
            jsonObject.put("description", todo.description)
            jsonObject.put("completed", todo.completed)
            jsonArray.put(jsonObject)
        }
        val json = jsonArray.toString()
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(json.toByteArray())
        }
    }

    fun loadTodosFromUri(context: Context, uri: Uri): List<TodoItem>? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            val todos = mutableListOf<TodoItem>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("id")
                val description = jsonObject.getString("description")
                val completed = jsonObject.getBoolean("completed")
                todos.add(TodoItem(id, description, completed))
            }
            todos
        }
    }
}