package com.paulomoura.desafiomobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.paulomoura.desafiomobile.databinding.ActivityRegistrationBinding
import com.paulomoura.desafiomobile.extension.bindings
import com.paulomoura.desafiomobile.extension.isEmailValid
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private val binding: ActivityRegistrationBinding by bindings(ActivityRegistrationBinding::inflate)

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener { validateInfo() }
    }

    private fun validateInfo() {
        val email = binding.tieEmail.text.toString().trim()
        val password = binding.tiePassword.text.toString().trim()
        if (!email.isEmailValid()) {
            Toast.makeText(this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Digite uma senha", Toast.LENGTH_SHORT).show()
            return
        }
        createUser(email, password)
    }

    private fun createUser(email: String, password: String) {
        binding.pbLoading.isVisible = true
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            binding.pbLoading.isVisible = false
            if (task.isSuccessful) showSuccessDialog() else showErrorDialog()
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Usuário cadastrado com sucesso")
            setPositiveButton("OK") { _, _ -> finish() }
            create()
        }.show()
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Erro ao cadastrar usuário")
            setPositiveButton("OK", null)
            create()
        }.show()
    }
}