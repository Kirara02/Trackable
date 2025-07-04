package com.uniguard.trackable.domain.uhf.repository

import com.uniguard.trackable.domain.uhf.model.InventoryTag
import kotlinx.coroutines.flow.StateFlow

interface UhfRepository {
    val isConnected: StateFlow<Boolean>
    val inventoryRunning: StateFlow<Boolean>
    val tagList: StateFlow<List<InventoryTag>>
    val selectedEpc: StateFlow<String?>
    val rssi: StateFlow<Int>
    val inventoryMode: StateFlow<Int>
    val enableLed: StateFlow<Boolean>

    fun initialize()
    fun connectReader(baudRate: Int): Boolean
    fun disconnect()
    fun startInventory(singleRead: Boolean = false)
    fun stopInventory()
    fun selectEpc(epc: String)
    fun clearTags()
    fun startFinding()
    fun stopFinding()
    suspend fun readFromTag(
        epc: String,
        memBank: Int,
        wordPtr: Int,
        length: Int,
        password: String
    ): Result<String>

    suspend fun writeToTag(
        epc: String,
        memBank: Int,
        wordPtr: Int,
        password: String,
        content: String
    ): Result<Unit>

    suspend fun writeEpcToTag(
        newEpc: String,
        password: String
    ): Result<Unit>

    fun setInventoryMode(mode: Int)
    fun setEnableLed(enabled: Boolean)
}