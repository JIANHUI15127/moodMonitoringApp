package com.example.moodmonitoringapp.viewModel.di

import android.content.Context
import com.example.moodmonitoringapp.data.SettingsRepository
import com.example.moodmonitoringapp.model.RecognizerName
import com.example.moodmonitoringapp.recognizer.LocalRecognizer
import com.example.moodmonitoringapp.recognizer.Recognizer
import com.example.moodmonitoringapp.recognizer.SkyBiometryRecognizer
import com.example.moodmonitoringapp.recognizer.tensorflow.EmotionClassifier
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RecognizerModule(private val context: Context) {

    @Provides
    fun provideRecognizer(settingsRepository: SettingsRepository): Recognizer {
        return when(settingsRepository.getRecognizer()) {
            RecognizerName.SKY_BIOMETRY -> {
                SkyBiometryRecognizer()
            }
            RecognizerName.LOCAL -> {
                LocalRecognizer()
            }
        }
    }

    @Provides
    @Singleton
    fun provideEmotionClassifier(): EmotionClassifier {
        return EmotionClassifier(context)
    }
}