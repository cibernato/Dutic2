package com.example.dutic2.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.log(message: String) {
    Log.e(this.javaClass.simpleName, message)
}