package com.example.androidclient

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/api/todos")
    suspend fun getTodos(
        @Header("Authorization") token: String
    ): List<Todo>


    @POST("/api/todos")
    suspend fun createTodo(
        @Body todo: Todo,
        @Header("Authorization") token: String
    ): Todo

    @PUT("/api/todos/{id}")
    suspend fun updateTodo(
        @Body todo: Todo,
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): Todo


}
