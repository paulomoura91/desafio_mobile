package com.paulomoura.desafiomobile.extension

import android.util.Patterns

fun String.isEmailValid(): Boolean {
    return isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}