package com.paulomoura.desafiomobile.extension

import android.view.LayoutInflater
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

@MainThread
inline fun <T : ViewBinding> AppCompatActivity.bindings(crossinline f: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) {
        f(layoutInflater)
    }