package com.paulomoura.desafiomobile

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.paulomoura.desafiomobile.service.LocationService.Companion.NOTIFICATION_CHANNEL_LOCATION
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by bindings(ActivityLoginBinding::inflate)

    @Inject
    lateinit var auth: FirebaseAuth

    @SuppressLint("InlinedApi")
    private val permissionsRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions[POST_NOTIFICATIONS]?.let { if (it) createChannel() }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkPermissions()
        createChannel()

        binding.btnLogin.setOnClickListener { loginUser() }
        binding.llRegistration.setOnClickListener { startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java)) }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()

        val locationPermissionsNotGranted = areLocationPermissionsNotGranted()
        if (locationPermissionsNotGranted) permissions.addAll(arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionNotGranted = isNotificationPermissionNotGranted()
            if (notificationPermissionNotGranted) permissions.add(POST_NOTIFICATIONS)
        }

        permissionsRequest.launch(permissions.toTypedArray())
    }

    private fun areLocationPermissionsNotGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isNotificationPermissionNotGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    }

    private fun createChannel() {
        val isAPI33OrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        val isApi26OrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        val notificationPermissionGranted =
            isAPI33OrHigher && ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

        if (notificationPermissionGranted || (isApi26OrHigher && !isAPI33OrHigher)) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_LOCATION, NOTIFICATION_CHANNEL_LOCATION, NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun loginUser() {
        if (
            areLocationPermissionsNotGranted() ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isNotificationPermissionNotGranted())
        ) {
            showPermissionsNotGrantedDialog()
        } else {
            val email = binding.tieEmail.text.toString().trim()
            val password = binding.tiePassword.text.toString().trim()
            if (isValidInfo(email, password)) performLogin(email, password)
        }
    }

    private fun showPermissionsNotGrantedDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Permissões Negadas")
            setMessage(
                StringBuffer().apply {
                    append("É necessário conceder as permissões de localização e notificação para funcionamento do App\n")
                    append("Abra as configurações do seu dispositivo para concedê-las")
                }.toString()
            )
            setPositiveButton("OK", null)
            create()
        }.show()
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
        startActivity(Intent(this, MapsActivity::class.java))
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