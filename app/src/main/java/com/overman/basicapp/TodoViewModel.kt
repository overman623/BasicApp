package com.overman.basicapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(private val todoRemoteRepository: TodoRemoteRepository, private val todoLocalRepository: TodoLocalRepository) : ViewModel() {

    private val _todoRemoteData = MutableLiveData<List<Todo>>()
    val todoRemoteData : LiveData<List<Todo>>
        get() = _todoRemoteData

    private val _todoLocalData = MutableLiveData<List<Todo>>()
    val todoLocalData : LiveData<List<Todo>>
        get() = _todoLocalData


    init {
        viewModelScope.launch {
            val remoteData = todoRemoteRepository.getData()
            withContext(Dispatchers.Main) {
                _todoRemoteData.postValue(remoteData)
            }

            val localData = todoLocalRepository.getData()
            withContext(Dispatchers.Main) {
                _todoLocalData.postValue(localData)
            }
        }
    }

    fun addLocalData(todo: Todo, action: (Todo) -> Unit) {
        viewModelScope.launch {
            todoLocalRepository.addData(todo)
            action.invoke(todo)
        }
    }

    fun deleteLocalData(todo: Todo, action: (Todo) -> Unit) {
        viewModelScope.launch {
            todoLocalRepository.deleteData(todo)
            action.invoke(todo)
        }
    }

    fun loadTodoData() = viewModelScope.launch {
        _todoRemoteData.postValue(todoRemoteRepository.getData())
    }


}