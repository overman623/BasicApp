package com.overman.basicapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Inject

interface TodoRemoteService {
    @GET("todos")
    suspend fun getTodoData(): Response<List<Todo>>
}

interface TodoRemoteRepository {
    suspend fun getData(): List<Todo>
}

class TodoRemoteRepositoryImpl @Inject constructor(private val todoRemoteService: TodoRemoteService) : TodoRemoteRepository {
    override suspend fun getData(): List<Todo> {

        return withContext(Dispatchers.IO) {
            val call = todoRemoteService.getTodoData()
            if (call.isSuccessful) {
                call.body() ?: listOf()
            } else {
                listOf()
            }
        }
    }

}
