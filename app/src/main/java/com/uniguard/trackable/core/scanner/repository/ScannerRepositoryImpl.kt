package com.uniguard.trackable.core.scanner.repository

import android.graphics.Bitmap
import com.uniguard.trackable.core.scanner.ScannerManager
import com.uniguard.trackable.domain.scanner.model.ScanResult
import com.uniguard.trackable.domain.scanner.repository.ScannerRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ScannerRepositoryImpl @Inject constructor(
    private val manager: ScannerManager
) : ScannerRepository {
    override val scanResult: StateFlow<ScanResult?> = manager.scanResult
    override val scanBitmap: StateFlow<Bitmap?> = manager.scanBitmap

    override fun startScan() = manager.startScan()
    override fun stopScan() = manager.stopScan()
    override fun registerReceiver() = manager.registerReceiver()
    override fun unregisterReceiver() = manager.unregisterReceiver()
}