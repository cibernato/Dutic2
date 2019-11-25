package com.example.dutic2.models

import java.io.Serializable

class Publicacion(
    var uid : String? = null,
    var tipo : Int? = 1, // 1 para normal, 2 con tarea
    var titulo:String? = null,
    var fecha : String? = null ,
    var tieneAdjuntos : Boolean? = false,
    var contenido : String? = null,
    var adjuntos : List<String>? = listOf()
    ) : Serializable{

}