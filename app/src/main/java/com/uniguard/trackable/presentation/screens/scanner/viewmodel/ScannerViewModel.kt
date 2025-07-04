package com.uniguard.trackable.presentation.screens.scanner.viewmodel

import androidx.lifecycle.ViewModel
import com.uniguard.trackable.domain.scanner.repository.ScannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: ScannerRepository
) : ViewModel() {
    val scanResult = repository.scanResult
    val scanBitmap = repository.scanBitmap

    fun startScan() = repository.startScan()
    fun stopScan() = repository.stopScan()
    fun registerReceiver() = repository.registerReceiver()
    fun unregisterReceiver() = repository.unregisterReceiver()
}