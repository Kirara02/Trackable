package com.uniguard.trackable.di

import android.content.Context
import com.uniguard.trackable.data.scanner.ScannerManager
import com.uniguard.trackable.data.uhf.UhfManager
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
    fun provideScannerManager(@ApplicationContext context: Context): ScannerManager {
        return ScannerManager(context)
    }

    @Provides
    @Singleton
    fun provideUhfManager(@ApplicationContext context: Context): UhfManager {
        return UhfManager(context)
    }
}