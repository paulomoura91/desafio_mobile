package com.paulomoura.desafiomobile.activity

import android.app.Activity
import android.app.Application
import android.text.SpannableStringBuilder
import android.widget.Button
import androidx.test.core.app.ApplicationProvider
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
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowToast

@HiltAndroidTest
@UninstallModules(AppModule::class)
@Config(application = HiltTestApplication::class, sdk = [32])
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

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
    fun `Teste de login com sucesso`() {
        val application: Application = ApplicationProvider.getApplicationContext()
        val app: ShadowApplication = shadowOf(application)
        app.grantPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        val task: Task<AuthResult> = mockk(relaxed = true)
        val onCompleteListenerSlot = slot<OnCompleteListener<AuthResult>>()

        every { auth.signInWithEmailAndPassword(any(), any()) } returns task
        every {
            task.addOnCompleteListener(any<Activity>(), capture(onCompleteListenerSlot))
        } answers {
            SuccessfulTask().also { onCompleteListenerSlot.captured.onComplete(it) }
        }

        val scenario = launchActivity<LoginActivity>()
        scenario.onActivity { activity ->
            val tieEmail = activity.findViewById<TextInputEditText>(R.id.tie_email)
            val tiePassword = activity.findViewById<TextInputEditText>(R.id.tie_password)
            val btnLogin = activity.findViewById<Button>(R.id.btn_login)

            tieEmail.text = SpannableStringBuilder("email@email.com")
            tiePassword.text = SpannableStringBuilder("senha")
            btnLogin.performClick()

            val intent = shadowOf(activity).nextStartedActivity
            val shadowIntent = shadowOf(intent)
            Assert.assertEquals(MapsActivity::class.java, shadowIntent.intentClass)
        }
    }

    @Test
    fun `Teste de login com erro`() {
        val application: Application = ApplicationProvider.getApplicationContext()
        val app: ShadowApplication = shadowOf(application)
        app.grantPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        val task: Task<AuthResult> = mockk(relaxed = true)
        val onCompleteListenerSlot = slot<OnCompleteListener<AuthResult>>()

        every { auth.signInWithEmailAndPassword(any(), any()) } returns task
        every {
            task.addOnCompleteListener(any<Activity>(), capture(onCompleteListenerSlot))
        } answers {
            UnsuccessfulTask().also { onCompleteListenerSlot.captured.onComplete(it) }
        }

        val scenario = launchActivity<LoginActivity>()
        scenario.onActivity { activity ->
            val tieEmail = activity.findViewById<TextInputEditText>(R.id.tie_email)
            val tiePassword = activity.findViewById<TextInputEditText>(R.id.tie_password)
            val btnLogin = activity.findViewById<Button>(R.id.btn_login)

            tieEmail.text = SpannableStringBuilder("email@email.com")
            tiePassword.text = SpannableStringBuilder("senha")
            btnLogin.performClick()

            val toastErrorMessage = ShadowToast.getTextOfLatestToast()
            Assert.assertEquals("Erro ao entrar na aplicação", toastErrorMessage)
        }
    }
}