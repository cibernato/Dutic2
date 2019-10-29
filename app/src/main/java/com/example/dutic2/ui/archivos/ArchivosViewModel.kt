package com.example.dutic2.ui.archivos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArchivosViewModel : ViewModel() {
    private val text = MutableLiveData<String>().apply {
        value = "Funciona y entiendo xd"
    }
    val t : LiveData<String> = text
}
