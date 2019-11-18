package com.example.dutic2.models

import java.io.Serializable

class Curso(
    var uid: String? = null,
    var nombre: String? = null,
    var profesor: String? = null,
    var tareasPendientes: String? = "0"
) : Serializable