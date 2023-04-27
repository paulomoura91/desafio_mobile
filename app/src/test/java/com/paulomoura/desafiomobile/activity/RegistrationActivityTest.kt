package com.paulomoura.desafiomobile.activity

import android.app.Activity
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.paulomoura.desafiomobile.R
import com.paulomoura.desafiomobile.data.dao.UserLocationDao
import com.paulomoura.desafiomobile.di.AppModule
import com.paulomoura.desafiomobile.util.SuccessfulTask
import com.paulomoura.desafiomobile.util.UnsuccessfulTask
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlertDialog

@HiltAndroidTest
@UninstallModules(AppModule::class)
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class RegistrationActivityTest {

    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @BindValue
    val auth: FirebaseAuth = mockk(relaxed = true)

    @BindValue
    val analytics: FirebaseAnalytics = mockk(relaxed = true)

    @BindValue
    val crashlytics: FirebaseCrashlytics = mockk(relaxed = true)

    @BindValue
    val userLocationDao: UserLocationDao = mockk(relaxed = true)

    @Before
    fun `Injetando dependencias mockadas`() = hiltAndroidRule.inject()

    @Test
    fun `Teste de cadastro com sucesso`() {
        val task: Task<AuthResult> = mockk(relaxed = true)
        val onCompleteListenerSlot = slot<OnCompleteListener<AuthResult>>()

        every { auth.createUserWithEmailAndPassword(any(), any()) } returns task
        every {
            task.addOnCompleteListener(any<Activity>(), capture(onCompleteListenerSlot))
        } answers {
            SuccessfulTask().also { onCompleteListenerSlot.captured.onComplete(it) }
        }

        val scenario = launchActivity<RegistrationActivity>()
        scenario.onActivity { activity ->
            val tieEmail = activity.findViewById<TextInputEditText>(R.id.tie_email)
            val tiePassword = activity.findViewById<TextInputEditText>(R.id.tie_password)
            val btnRegister = activity.findViewById<Button>(R.id.btn_register)

            tieEmail.text = SpannableStringBuilder("email@email.com")
            tiePassword.text = SpannableStringBuilder("senha")
            btnRegister.performClick()

            val successDialog = ShadowAlertDialog.getShownDialogs().first()
            val messageTextView = successDialog.findViewById<TextView>(android.R.id.message)
            Assert.assertEquals("Usuário cadastrado com sucesso", messageTextView.text)
        }
    }

    @Test
    fun `Teste de cadastro com erro`() {
        val task: Task<AuthResult> = mockk(relaxed = true)
        val onCompleteListenerSlot = slot<OnCompleteListener<AuthResult>>()

        every { auth.createUserWithEmailAndPassword(any(), any()) } returns task
        every {
            task.addOnCompleteListener(any<Activity>(), capture(onCompleteListenerSlot))
        } answers {
            UnsuccessfulTask().also { onCompleteListenerSlot.captured.onComplete(it) }
        }

        val scenario = launchActivity<RegistrationActivity>()
        scenario.onActivity { activity ->
            val tieEmail = activity.findViewById<TextInputEditText>(R.id.tie_email)
            val tiePassword = activity.findViewById<TextInputEditText>(R.id.tie_password)
            val btnRegister = activity.findViewById<Button>(R.id.btn_register)

            tieEmail.text = SpannableStringBuilder("email@email.com")
            tiePassword.text = SpannableStringBuilder("senha")
            btnRegister.performClick()

            val successDialog = ShadowAlertDialog.getShownDialogs().first()
            val messageTextView = successDialog.findViewById<TextView>(android.R.id.message)
            Assert.assertEquals("Erro ao cadastrar usuário", messageTextView.text)
        }
    }
}