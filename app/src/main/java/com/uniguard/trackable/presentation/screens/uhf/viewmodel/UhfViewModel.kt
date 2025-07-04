package com.uniguard.trackable.presentation.screens.uhf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniguard.trackable.domain.uhf.repository.UhfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UhfViewModel @Inject constructor(
    private val repository: UhfRepository
) : ViewModel() {

    init {
        initialize()
    }

    val isConnected = repository.isConnected
    val inventoryRunning = repository.inventoryRunning
    val tagList = repository.tagList
    val selectedEpc = repository.selectedEpc
    val rssi = repository.rssi
    val inventoryMode = repository.inventoryMode
    val enableLed = repository.enableLed

    private fun initialize() = repository.initialize()
    fun setInventoryMode(mode: Int) = repository.setInventoryMode(mode)
    fun setEnableLed(enabled: Boolean) = repository.setEnableLed(enabled)
    fun connectReader(baudRate: Int) = repository.connectReader(baudRate)
    fun disconnect() = repository.disconnect()
    fun startInventory(singleRead: Boolean = false) = repository.startInventory(singleRead)
    fun stopInventory() = repository.stopInventory()
    fun selectEpc(epc: String) = repository.selectEpc(epc)
    fun clearTags() = repository.clearTags()
    fun startFinding() = repository.startFinding()
    fun stopFinding() = repository.stopFinding()

    // ✅ READ from tag
    fun readFromTag(
        epc: String,
        memBank: Int,
        wordPtr: Int,
        length: Int,
        password: String,
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.readFromTag(epc, memBank, wordPtr, length, password)
            onResult(result)
        }
    }

    // ✅ WRITE to tag
    fun writeToTag(
        epc: String,
        memBank: Int,
        wordPtr: Int,
        password: String,
        content: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.writeToTag(epc, memBank, wordPtr, password, content)
            onResult(result)
        }
    }

    // ✅ WRITE EPC
    fun writeEpcToTag(
        newEpc: String,
        password: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.writeEpcToTag(newEpc, password)
            onResult(result)
        }
    }
}
