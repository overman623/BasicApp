package com.overman.basicapp

import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "Todo.db"
        const val TABLE_NAME_TODO = "tb_todo"
    }

    abstract fun todoDAO() : TodoDAO

}

@Dao
interface TodoDAO {
    @Query("SELECT * FROM ${TodoDatabase.TABLE_NAME_TODO}")
    fun getTodoList() : List<Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(todo: Todo) : Long

    @Delete
    fun delete(todo: Todo)
}

interface TodoLocalRepository {
    suspend fun getData(): List<Todo>
    suspend fun addData(todo: Todo)
    suspend fun deleteData(todo: Todo)
}

class TodoLocalRepositoryImpl @Inject constructor(private val todoDAO: TodoDAO) : TodoLocalRepository {
    override suspend fun getData(): List<Todo> {
        return withContext(Dispatchers.IO) {
            todoDAO.getTodoList()
        }
    }

    override suspend fun addData(todo: Todo) {
        withContext(Dispatchers.IO) {
            todoDAO.insert(todo)
        }
    }

    override suspend fun deleteData(todo: Todo) {
        withContext(Dispatchers.IO) {
            todoDAO.delete(todo)
        }
    }
}
