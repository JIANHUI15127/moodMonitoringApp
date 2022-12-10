package com.example.moodmonitoringapp.viewModel.di

import com.example.moodmonitoringapp.data.ApiKeyRepository
import com.example.moodmonitoringapp.network.AuthInterceptor
import com.example.moodmonitoringapp.network.NetworkService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {

    @Provides
    fun provideOkHttpClient(apiKeyRepository: ApiKeyRepository): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(apiKeyRepository))
        .build()

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.skybiometry.com/fc/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Provides
    fun provideNetworkService(retrofit: Retrofit) : NetworkService
            = retrofit.create(NetworkService::class.java)
}