package com.uniguard.trackable.di

import com.uniguard.trackable.data.remote.api.ApiService
import com.uniguard.trackable.data.remote.repository.AuthRepositoryImpl
import com.uniguard.trackable.core.scanner.ScannerManager
import com.uniguard.trackable.core.scanner.repository.ScannerRepositoryImpl
import com.uniguard.trackable.core.uhf.UhfManager
import com.uniguard.trackable.core.uhf.repository.UhfRepositoryImpl
import com.uniguard.trackable.domain.repository.AuthRepository
import com.uniguard.trackable.domain.scanner.repository.ScannerRepository
import com.uniguard.trackable.domain.uhf.repository.UhfRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideScannerRepository(
        scannerManager: ScannerManager
    ): ScannerRepository {
        return ScannerRepositoryImpl(scannerManager)
    }

    @Provides
    @Singleton
    fun provideUhfRepository(
        uhfManager: UhfManager
    ): UhfRepository {
        return UhfRepositoryImpl(uhfManager)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService
    ): AuthRepository {
        return AuthRepositoryImpl(apiService)
    }


}