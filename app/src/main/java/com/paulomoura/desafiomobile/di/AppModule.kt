package com.paulomoura.desafiomobile.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.paulomoura.desafiomobile.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseCrashLytics() = Firebase.crashlytics

    @Provides
    @Singleton
    fun provideFirebaseAnalytics() = Firebase.analytics

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(appContext, AppDatabase::class.java, "appDb").build()
    }

    @Provides
    @Singleton
    fun provideUserLocationDao(appDatabase: AppDatabase) = appDatabase.userLocationDao()
}