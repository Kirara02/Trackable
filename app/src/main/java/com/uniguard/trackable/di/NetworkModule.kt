package com.uniguard.trackable.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.uniguard.trackable.BuildConfig
import com.uniguard.trackable.data.local.datastore.PreferenceManager
import com.uniguard.trackable.data.remote.api.ApiService
import com.uniguard.trackable.data.remote.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://ugz-api-668795567730.asia-southeast1.run.app"

    @Provides
    @Singleton
    fun providerGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideAuthInterceptor(preferenceManager: PreferenceManager): AuthInterceptor {
        return AuthInterceptor(preferenceManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if(BuildConfig.DEBUG){
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

}