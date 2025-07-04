package com.uniguard.trackable.data.uhf

import android.content.Context
import com.rfid.trans.ReadTag
import com.rfid.trans.TagCallback
import com.uniguard.trackable.domain.uhf.model.InventoryTag
import com.uniguard.trackable.utils.Reader
import com.uniguard.trackable.utils.SoundUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UhfManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _inventoryRunning = MutableStateFlow(false)
    val inventoryRunning: StateFlow<Boolean> = _inventoryRunning

    private val _tagList = MutableStateFlow<List<InventoryTag>>(emptyList())
    val tagList: StateFlow<List<InventoryTag>> = _tagList

    private val _selectedEpc = MutableStateFlow<String?>(null)
    val selectedEpc: StateFlow<String?> = _selectedEpc

    private val _rssi = MutableStateFlow(0)
    val rssi: StateFlow<Int> = _rssi

    private val _inventoryMode = MutableStateFlow(1) // 0 = Single, 1 = Loop
    val inventoryMode: StateFlow<Int> = _inventoryMode

    private val _enableLed = MutableStateFlow(false)
    val enableLed: StateFlow<Boolean> = _enableLed

    private var findingJob: Job? = null

    fun initialize() {
        SoundUtils.initSound(context)
        SoundUtils.setupReaderSound()
    }

    fun connectReader(baudRate: Int): Boolean {
        val port = "/dev/ttyHSL0"
        val result = Reader.rrlib.Connect(port, baudRate, 1)
        if (result == 0) {
            _isConnected.value = true
            initRfidParams()
        }
        return result == 0
    }

    fun disconnect() {
        Reader.rrlib.DisConnect()
        _isConnected.value = false
        SoundUtils.release()
    }

    private fun initRfidParams() {
        val readerType = Reader.rrlib.GetReaderType()
        val param = Reader.rrlib.GetInventoryPatameter()
        param.Session = when (readerType) {
            0x21, 0x28, 0x23, 0x37, 0x36 -> 1
            0x70, 0x71, 0x31 -> 254
            0x61, 0x63, 0x65, 0x66 -> 1
            else -> 0
        }
        Reader.rrlib.SetInventoryPatameter(param)
    }

    fun startInventory(singleRead: Boolean = false) {
        _tagList.value = emptyList()

        Reader.rrlib.SetCallBack(object : TagCallback {
            override fun tagCallback(tag: ReadTag?) {
                tag?.let {
                    val epc = it.epcId ?: return
                    val mem = it.memId ?: ""
                    val rssi = it.rssi

                    val current = _tagList.value.toMutableList()
                    val index = current.indexOfFirst { t -> t.epc == epc }

                    if (index != -1) {
                        current[index] = current[index].copy(
                            count = current[index].count + 1,
                            rssi = rssi
                        )
                    } else {
                        current.add(InventoryTag(epc, mem, 1, rssi))
                    }

                    _tagList.value = current
                }
            }

            override fun StopReadCallBack() {
                _inventoryRunning.value = false
            }
        })

        if (singleRead) {
            Reader.rrlib.ScanRfid()
        } else {
            Reader.rrlib.StartRead()
        }

        _inventoryRunning.value = true
    }

    fun stopInventory() {
        Reader.rrlib.StopRead()
        _inventoryRunning.value = false
    }

    fun selectEpc(epc: String) {
        _selectedEpc.value = epc
    }

    fun clearTags() {
        _tagList.value = emptyList()
    }

    fun startFinding() {
        val epc = _selectedEpc.value ?: _tagList.value.firstOrNull()?.epc ?: return
        _selectedEpc.value = epc

        findingJob?.cancel()
        findingJob = scope.launch {
            while (true) {
                val tag = Reader.rrlib.FindEPC(epc)
                if (tag != null) {
                    _rssi.value = tag.rssi
                    Reader.rrlib.playSound()
                } else {
                    _rssi.value = (_rssi.value - 2).coerceAtLeast(0)
                }
                delay(100)
            }
        }
    }

    fun stopFinding() {
        findingJob?.cancel()
        findingJob = null
    }

    suspend fun readFromTag(
        epc: String,
        memBank: Int,
        wordPtr: Int,
        length: Int,
        password: String
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val data = Reader.rrlib.ReadData_G2(
                epc,
                memBank.toByte(),
                wordPtr,
                length.toByte(),
                password
            )
            if (data == null) Result.failure(Exception("Read failed"))
            else Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun writeToTag(
        epc: String,
        memBank: Int,
        wordPtr: Int,
        password: String,
        content: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = Reader.rrlib.WriteData_G2(
                content,
                epc,
                memBank.toByte(),
                wordPtr,
                password
            )
            if (result == 0) Result.success(Unit)
            else Result.failure(Exception("Write failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun writeEpcToTag(
        newEpc: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = Reader.rrlib.WriteEPC_G2(newEpc, password)
            if (result == 0) Result.success(Unit)
            else Result.failure(Exception("Write EPC failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun setInventoryMode(mode: Int) {
        _inventoryMode.value = mode
    }

    fun setEnableLed(enabled: Boolean) {
        _enableLed.value = enabled
    }
}
