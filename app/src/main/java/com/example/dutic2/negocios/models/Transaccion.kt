package com.example.dutic2.negocios.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Transaccion(
    var id: String = "",
    var horas: String = "",
    var codigo: String = "",
    var estado: String = "",
    var codigoPermiso: String = "",
    var codigoTrabajador: String = "",
    var estadoPermiso: String = "",
    var nombrePermiso: String = "",
    var nombreTrabajador: String = "",
    var estadoTrabajdor: String = ""
) : Parcelable {
}