package com.paulomoura.desafiomobile.util

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import io.mockk.mockk
import java.util.concurrent.Executor

class SuccessfulTask : MockTask() {

    override fun isSuccessful() = true
}

class UnsuccessfulTask : MockTask() {

    override fun isSuccessful() = false
}

abstract class MockTask : Task<AuthResult>() {

    override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> = mockk()

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<AuthResult> = mockk()

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<AuthResult> = mockk()

    override fun getException(): Exception? = mockk()

    override fun getResult(): AuthResult = mockk()

    override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult = mockk()

    override fun isCanceled(): Boolean = mockk()

    override fun isComplete(): Boolean = mockk()

    override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> = mockk()

    override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> = mockk()

    override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> = mockk()
}