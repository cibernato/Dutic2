package com.example.dutic2.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dutic2.models.Curso
import kotlin.random.Random

class HomeViewModel : ViewModel() {

    private val random = Random(20)

    private val _cursos = MutableLiveData<ArrayList<Curso>>().apply {
        value = ArrayList()
        for (i in 1..random.nextInt(20)) {
            value?.add(
                Curso(
                    "Nombre ${random.nextInt(50)}",
                    "Profesor ${random.nextInt(50)}"
                )
            )
        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "Primera app usando MVVM"

    }

    val text: LiveData<String> = _text
    fun getCursos(): MutableLiveData<ArrayList<Curso>> {
        return _cursos
    }

}