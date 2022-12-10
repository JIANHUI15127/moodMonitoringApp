package com.example.moodmonitoringapp.viewModel.di

import com.example.moodmonitoringapp.recognizer.LocalRecognizer
import com.example.moodmonitoringapp.recognizer.SkyBiometryRecognizer
import com.example.moodmonitoringapp.viewModel.ImageViewModel
import com.example.moodmonitoringapp.viewModel.MainViewModel
import dagger.Component

import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RepositoryModule::class, RecognizerModule::class])
interface AppComponent {
    fun inject(mainViewModel: MainViewModel)
    fun inject(skyBiometryRecognizer: SkyBiometryRecognizer)
    fun inject(imageViewModel: ImageViewModel)
    fun inject(localRecognizer: LocalRecognizer)
}