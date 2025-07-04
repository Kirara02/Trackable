package com.uniguard.trackable.domain.scanner.repository

import android.graphics.Bitmap
import com.uniguard.trackable.domain.scanner.model.ScanResult
import kotlinx.coroutines.flow.StateFlow

interface ScannerRepository {
    val scanResult: StateFlow<ScanResult?>
    val scanBitmap: StateFlow<Bitmap?>
    fun startScan(): Boolean
    fun stopScan(): Boolean
    fun registerReceiver()
    fun unregisterReceiver()
}