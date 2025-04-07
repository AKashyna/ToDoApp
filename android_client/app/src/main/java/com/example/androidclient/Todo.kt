package com.example.androidclient

data class Todo(
    val id: Long? = null,
    val title: String,
    val done: Boolean,
    val createdAt: String? = null
)
