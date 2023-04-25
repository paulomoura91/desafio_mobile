package com.paulomoura.desafiomobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.paulomoura.desafiomobile.databinding.ActivityLoginBinding
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
        binding.btnSignin.setOnClickListener { signInUser() }
        binding.llRegistration.setOnClickListener { startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java)) }
    }

    private fun signInUser() {
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
        //analytics
        //salvar os dados no shared preferences
    }

    private fun handleLoginError() {
        toast("Erro ao entrar na aplicação")
        //crashlytics
    }
}