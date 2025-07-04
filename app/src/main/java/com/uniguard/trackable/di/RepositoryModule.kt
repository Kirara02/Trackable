package com.uniguard.trackable.di

import com.uniguard.trackable.data.scanner.ScannerManager
import com.uniguard.trackable.data.scanner.repository.ScannerRepositoryImpl
import com.uniguard.trackable.data.uhf.UhfManager
import com.uniguard.trackable.data.uhf.repository.UhfRepositoryImpl
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


}