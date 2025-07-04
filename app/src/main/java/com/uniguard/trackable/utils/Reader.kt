package com.uniguard.trackable.utils

import android.content.Context
import android.device.DeviceManager
import com.rfid.trans.ReaderHelp

object Reader {
    var rrlib: ReaderHelp = ReaderHelp()

    fun setOpenScan523(context: Context, isOpen: Boolean) {
        try {
            val manager = DeviceManager()
            if (isOpen) {
                // Enable auto scan on key 523
                manager.setSettingProperty("persist-persist.sys.rfid.key", "0-")
                manager.setSettingProperty("persist-persist.sys.scan.key", "520-521-522-523-")
            } else {
                // Disable auto scan on key 523
                manager.setSettingProperty("persist-persist.sys.rfid.key", "0-")
                manager.setSettingProperty("persist-persist.sys.scan.key", "520-521-522-")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}