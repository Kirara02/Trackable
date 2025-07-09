package com.uniguard.trackable.core.uhf.repository

import com.uniguard.trackable.core.uhf.UhfManager
import com.uniguard.trackable.domain.uhf.model.InventoryTag
import com.uniguard.trackable.domain.uhf.repository.UhfRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UhfRepositoryImpl @Inject constructor(
    private val manager: UhfManager
) : UhfRepository {
    override val isConnected: StateFlow<Boolean> = manager.isConnected
    override val inventoryRunning: StateFlow<Boolean> = manager.inventoryRunning
    override val tagList: StateFlow<List<InventoryTag>> = manager.tagList
    override val selectedEpc: StateFlow<String?> = manager.selectedEpc
    override val rssi: StateFlow<Int> = manager.rssi
    override val inventoryMode: StateFlow<Int> = manager.inventoryMode
    override val enableLed: StateFlow<Boolean> = manager.enableLed

    override fun initialize() = manager.initialize()
    override fun connectReader(baudRate: Int) = manager.connectReader(baudRate)
    override fun disconnect() = manager.disconnect()
    override fun startInventory(singleRead: Boolean) = manager.startInventory(singleRead)
    override fun stopInventory() = manager.stopInventory()
    override fun selectEpc(epc: String) = manager.selectEpc(epc)
    override fun clearTags() = manager.clearTags()
    override fun startFinding() = manager.startFinding()
    override fun stopFinding() = manager.stopFinding()
    override suspend fun readFromTag(
        epc: String,
        memBank: Int,
        wordPtr: Int,
        length: Int,
        password: String
    ): Result<String> = manager.readFromTag(epc, memBank, wordPtr, length, password)

    override suspend fun writeToTag(
        epc: String,
        memBank: Int,
        wordPtr: Int,
        password: String,
        content: String
    ): Result<Unit> = manager.writeToTag(epc, memBank, wordPtr, password, content)

    override suspend fun writeEpcToTag(
        newEpc: String,
        password: String
    ): Result<Unit> = manager.writeEpcToTag(newEpc, password)

    override fun setInventoryMode(mode: Int) = manager.setInventoryMode(mode)
    override fun setEnableLed(enabled: Boolean) = manager.setEnableLed(enabled)

}