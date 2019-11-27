package com.example.dutic2.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Curso(
    var uid: String? = null,
    var nombre: String? = null,
    var profesor: String? = null,
    var tareasPendientes: String? = "0",
    var idGeneral :String? = null,
    var tareasTotales : String? = null,
    var tareasEntregadasNumero : String? = null,
    var promedios : String? = "[]",
    var promedioTotal : String? =null
) : Parcelable{
    override fun toString(): String {
        return "$uid, $nombre, $profesor, $tareasPendientes, $idGeneral, $tareasTotales, $tareasEntregadasNumero"
    }


}