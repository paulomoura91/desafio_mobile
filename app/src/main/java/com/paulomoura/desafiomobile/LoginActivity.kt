package com.paulomoura.desafiomobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paulomoura.desafiomobile.databinding.ActivityLoginBinding
import com.paulomoura.desafiomobile.extension.bindings

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by bindings(ActivityLoginBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.llRegistration.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
        }
    }
}