// data/scanner/ScannerManager.kt
package com.uniguard.trackable.core.scanner

import android.content.*
import android.device.ScanManager
import android.device.scanner.configuration.Triggering
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.uniguard.trackable.domain.scanner.model.ScanResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScannerManager(private val context: Context) {

    companion object {
        private const val TAG = "ScannerManager"

        const val ACTION_DECODE = ScanManager.ACTION_DECODE
        const val ACTION_DECODE_IMAGE_REQUEST = "action.scanner_capture_image"
        const val ACTION_CAPTURE_IMAGE = "scanner_capture_image_result"

        const val DECODE_DATA_TAG = ScanManager.DECODE_DATA_TAG
        const val BARCODE_LENGTH_TAG = ScanManager.BARCODE_LENGTH_TAG
        const val BARCODE_STRING_TAG = ScanManager.BARCODE_STRING_TAG
        const val DECODE_CAPTURE_IMAGE_KEY = "bitmapBytes"
        const val DECODE_OUTPUT_MODE_INTENT = 0

        const val MSG_SHOW_SCAN_RESULT = 1
        const val MSG_SHOW_SCAN_IMAGE = 2
    }

    private val scanManager = ScanManager()

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult

    private val _scanBitmap = MutableStateFlow<Bitmap?>(null)
    val scanBitmap: StateFlow<Bitmap?> = _scanBitmap

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            MSG_SHOW_SCAN_RESULT -> {
                val result = msg.obj as ScanResult
                Log.d(TAG, "📥 New scan result received:\n$result")
                _scanResult.value = result
            }

            MSG_SHOW_SCAN_IMAGE -> {
                val bitmap = msg.obj as? Bitmap
                Log.d(TAG, "🖼️ New scan image received")
                _scanBitmap.value = bitmap
            }
        }
        true
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_CAPTURE_IMAGE -> {
                    val imageData = intent.getByteArrayExtra(DECODE_CAPTURE_IMAGE_KEY)
                    imageData?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        handler.sendMessage(handler.obtainMessage(MSG_SHOW_SCAN_IMAGE, bitmap))
                    }
                }

                ACTION_DECODE -> {
                    val barcode = intent.getByteArrayExtra(DECODE_DATA_TAG)
                    val barcodeLen = intent.getIntExtra(BARCODE_LENGTH_TAG, 0)
                    val barcodeStr = intent.getStringExtra(BARCODE_STRING_TAG)

                    if (barcode != null) {
                        val hexString = bytesToHexString(barcode)
                        val resultString = String(barcode, 0, barcodeLen)
                        val printable = bytesHexToPrintableString(barcode)

                        val result = ScanResult(
                            rawData = resultString,
                            length = barcodeLen,
                            hex = hexString,
                            printable = printable,
                            extraStr = barcodeStr ?: ""
                        )

                        handler.sendMessage(handler.obtainMessage(MSG_SHOW_SCAN_RESULT, result))

                        Log.d(TAG, "📸 Requesting scan image capture...")
                        context.sendBroadcast(Intent(ACTION_DECODE_IMAGE_REQUEST))
                    } else {
                        Log.w(TAG, "⚠️ Null barcode received")
                    }
                }

                else -> Log.w(TAG, "⚠️ Unhandled action: ${intent.action}")
            }
        }
    }

    init {
        Log.d(TAG, "🚀 Initializing ScannerManager")
        setOutputMode(DECODE_OUTPUT_MODE_INTENT)
        setTriggerMode(Triggering.HOST)

        if (!scanManager.scannerState) {
            val opened = scanManager.openScanner()
            Log.d(TAG, "🔌 Scanner open state: $opened")
        }
    }

    fun startScan() = scanManager.startDecode()
    fun stopScan() = scanManager.stopDecode()

    fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(ACTION_DECODE)
            addAction(ACTION_CAPTURE_IMAGE)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                ContextCompat.registerReceiver(
                    context,
                    receiver,
                    filter,
                    ContextCompat.RECEIVER_EXPORTED
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error registering receiver", e)
        }
    }

    fun unregisterReceiver() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Error unregistering receiver", e)
        }
    }

    private fun setOutputMode(mode: Int) {
        val currentMode = scanManager.outputMode
        if (currentMode != mode) {
            scanManager.switchOutputMode(mode)
            Log.d(TAG, "✅ Output mode changed to INTENT")
        }
    }

    private fun setTriggerMode(mode: Triggering) {
        val currentMode = scanManager.triggerMode
        if (currentMode != mode) {
            scanManager.triggerMode = mode
            Log.d(TAG, "✅ Trigger mode set to HOST")
        }
    }

    private fun bytesToHexString(src: ByteArray?): String {
        if (src == null || src.isEmpty()) return ""
        return src.joinToString(" ") { String.format("%02X", it) }
    }

    private fun bytesHexToPrintableString(src: ByteArray?): String {
        if (src == null || src.isEmpty()) return ""
        return buildString {
            for (b in src) {
                val c = b.toInt().toChar()
                append(if (c.isLetterOrDigit() || c.isWhitespace() || c in '!'..'~') c else '.')
            }
        }
    }
}
