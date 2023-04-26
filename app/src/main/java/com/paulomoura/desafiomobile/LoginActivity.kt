package com.paulomoura.desafiomobile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.paulomoura.desafiomobile.constant.AnalyticsConstants
import com.paulomoura.desafiomobile.constant.SharedPreferencesConstants
import com.paulomoura.desafiomobile.databinding.ActivityLoginBinding
import com.paulomoura.desafiomobile.exception.LoginErrorException
import com.paulomoura.desafiomobile.extension.bindings
import com.paulomoura.desafiomobile.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by bindings(ActivityLoginBinding::inflate)

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener { loginUser() }
        binding.llRegistration.setOnClickListener { startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java)) }
    }

    private fun loginUser() {
        val email = binding.tieEmail.text.toString().trim()
        val password = binding.tiePassword.text.toString().trim()
        if (isValidInfo(email, password)) performLogin(email, password)
    }

    private fun isValidInfo(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            toast("Digite um e-mail")
            return false
        }
        if (password.isEmpty()) {
            toast("Digite uma senha")
            return false
        }
        return true
    }

    private fun performLogin(email: String, password: String) {
        binding.pbLoading.isVisible = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            binding.pbLoading.isVisible = false
            if (task.isSuccessful) handleLoginSuccess() else handleLoginError()
        }
    }

    private fun handleLoginSuccess() {
        startActivity(Intent(this, MapActivity::class.java))
        auth.currentUser?.let { firebaseUser ->
            saveUserData(firebaseUser)
            logLogin(firebaseUser)
        }
    }

    private fun saveUserData(firebaseUser: FirebaseUser) {
        val preferences = getSharedPreferences(SharedPreferencesConstants.GLOBAL_PREFERENCES, Context.MODE_PRIVATE)
        preferences?.let {
            with(it.edit()) {
                putString(SharedPreferencesConstants.KEY_FIREBASE_USER_ID, firebaseUser.uid)
                putString(SharedPreferencesConstants.KEY_FIREBASE_USER_EMAIL, firebaseUser.email)
                apply()
            }
        }
    }

    private fun logLogin(firebaseUser: FirebaseUser) {
        Firebase.analytics.logEvent(AnalyticsConstants.EVENT_LOGIN) {
            param(AnalyticsConstants.PARAM_USER_ID, firebaseUser.uid)
            param(AnalyticsConstants.PARAM_USER_EMAIL, firebaseUser.email ?: "")
        }
    }

    private fun handleLoginError() {
        toast("Erro ao entrar na aplicação")
        logLoginError()
    }

    private fun logLoginError() = Firebase.crashlytics.recordException(LoginErrorException())
}