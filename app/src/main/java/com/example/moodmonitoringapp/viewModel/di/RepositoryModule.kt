package com.example.moodmonitoringapp.viewModel.di

import android.content.Context
import com.example.moodmonitoringapp.data.ApiKeyRepository
import com.example.moodmonitoringapp.data.RecognizedRepository
import com.example.moodmonitoringapp.data.SettingsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideRecognizedRepository() = RecognizedRepository()

    @Provides
    fun provideApiKeyRepository() = ApiKeyRepository(context)

    @Provides
    fun provideSettingsRepository() = SettingsRepository(context)
}