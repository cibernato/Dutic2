package com.example.dutic2.negocios.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Permiso(
    var id: String = "",
    var nombre: String = "",
    var codigo: String = "",
    var estado: String = ""
) : Parcelable
