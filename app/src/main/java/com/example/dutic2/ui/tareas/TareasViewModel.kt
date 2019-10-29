package com.example.dutic2.ui.tareas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TareasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is tareas Fragment"
    }
    val text: LiveData<String> = _text
}