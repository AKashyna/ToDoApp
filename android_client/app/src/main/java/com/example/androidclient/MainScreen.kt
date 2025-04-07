package com.example.androidclient

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.example.androidclient.Todo
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(token: String) {
    var refreshTrigger by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ToDoApp") },
                actions = {
                    IconButton(onClick = {
                        Log.d("MainScreen", " Kliknięto odświeżanie")
                        refreshTrigger = !refreshTrigger
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Odśwież")
                    }
                }
            )
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.weight(1f)) {
                TodoListScreen(
                    token = token,
                    refreshTrigger = refreshTrigger,
                    onRefreshRequested = { refreshTrigger = !refreshTrigger }
                )
            }


            var newTodoText by remember { mutableStateOf("") }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = newTodoText,
                    onValueChange = { newTodoText = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Nowe zadanie") }
                )
                Button(onClick = {
                    if (newTodoText.isNotBlank()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                RetrofitInstance.api.createTodo(
                                    Todo(id = null, title = newTodoText, done = false, createdAt = null),
                                    "Bearer $token"
                                )

                                newTodoText = ""
                                withContext(Dispatchers.Main) {
                                    refreshTrigger = !refreshTrigger
                                }

                            } catch (e: Exception) {
                                Log.e("MainScreen", " Błąd dodawania zadania: ${e.message}")
                            }
                        }
                    }
                }) {
                    Text("Dodaj")
                }
            }


        }
    }
}

@Composable
fun TodoListScreen(
    token: String,
    refreshTrigger: Boolean,
    onRefreshRequested: () -> Unit
) {
    var todos by remember { mutableStateOf<List<Todo>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(token, refreshTrigger) {
        Log.d("TodoList", " Pobieranie zadań...")

        try {
            todos = RetrofitInstance.api.getTodos("Bearer $token")
        } catch (e: Exception) {
            Log.e("TodoList", " Błąd pobierania zadań: ${e.message}")
        } finally {
            loading = false
        }
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(todos, key = { it.id ?: it.title }) { todo ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = todo.done,
                        onCheckedChange = { checked ->
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    RetrofitInstance.api.updateTodo(
                                        todo.copy(done = checked),
                                        todo.id!!,
                                        "Bearer $token"
                                    )
                                    withContext(Dispatchers.Main) {
                                        onRefreshRequested()
                                    }

                                } catch (e: Exception) {
                                    Log.e("TodoList", " Błąd aktualizacji zadania: ${e.message}")
                                }
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4CAF50)
                        )
                    )

                    Text(
                        text = todo.title,
                        modifier = Modifier.padding(start = 8.dp),
                        style = if (todo.done) {
                            LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough)
                        } else {
                            LocalTextStyle.current
                        }
                    )

                }

            }
        }
    }
}



