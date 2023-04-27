package com.paulomoura.desafiomobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.paulomoura.desafiomobile.constant.AnalyticsConstants
import com.paulomoura.desafiomobile.databinding.ActivityRegistrationBinding
import com.paulomoura.desafiomobile.exception.RegisterUserErrorException
import com.paulomoura.desafiomobile.extension.bindings
import com.paulomoura.desafiomobile.extension.isEmailValid
import com.paulomoura.desafiomobile.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private val binding: ActivityRegistrationBinding by bindings(ActivityRegistrationBinding::inflate)

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var analytics: FirebaseAnalytics
    @Inject
    lateinit var crashlytics: FirebaseCrashlytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val email = binding.tieEmail.text.toString().trim()
        val password = binding.tiePassword.text.toString().trim()
        if (isValidInfo(email, password)) performRegistration(email, password)
    }

    private fun isValidInfo(email: String, password: String): Boolean {
        if (!email.isEmailValid()) {
            toast("Digite um e-mail válido")
            return false
        }
        if (password.isEmpty()) {
            toast("Digite uma senha")
            return false
        }
        return true
    }

    private fun performRegistration(email: String, password: String) {
        binding.pbLoading.isVisible = true
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            binding.pbLoading.isVisible = false
            if (task.isSuccessful) handleRegisterUserSuccess() else handleRegisterUserError()
        }
    }

    private fun handleRegisterUserSuccess() {
        showSuccessDialog()
        auth.currentUser?.let { firebaseUser ->
            logRegisterUser(firebaseUser)
        }
    }

    private fun logRegisterUser(firebaseUser: FirebaseUser) {
        analytics.logEvent(AnalyticsConstants.EVENT_REGISTER_USER) {
            param(AnalyticsConstants.PARAM_USER_ID, firebaseUser.uid)
            param(AnalyticsConstants.PARAM_USER_EMAIL, firebaseUser.email ?: "")
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Usuário cadastrado com sucesso")
            setPositiveButton("OK") { _, _ -> finish() }
            create()
        }.show()
    }

    private fun handleRegisterUserError() {
        showErrorDialog()
        logRegisterUserError()
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Erro ao cadastrar usuário")
            setPositiveButton("OK", null)
            create()
        }.show()
    }

    private fun logRegisterUserError() = crashlytics.recordException(RegisterUserErrorException())
}