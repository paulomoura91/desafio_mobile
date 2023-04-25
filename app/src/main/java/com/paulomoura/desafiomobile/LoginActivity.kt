package com.paulomoura.desafiomobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.paulomoura.desafiomobile.databinding.ActivityLoginBinding
import com.paulomoura.desafiomobile.extension.bindings
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
        binding.btnSignin.setOnClickListener { validateInfo() }
        binding.llRegistration.setOnClickListener { startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java)) }
    }

    private fun validateInfo() {
        val email = binding.tieEmail.text.toString().trim()
        val password = binding.tiePassword.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, "Digite um e-mail", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Digite uma senha", Toast.LENGTH_SHORT).show()
            return
        }
        performLogin(email, password)
    }

    private fun performLogin(email: String, password: String) {
        binding.pbLoading.isVisible = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            binding.pbLoading.isVisible = false
            if (task.isSuccessful) {
                startActivity(Intent(this, MapActivity::class.java))
            } else {
                Toast.makeText(this, "Erro ao entrar na aplicação", Toast.LENGTH_SHORT).show()
            }
        }
    }
}