package com.uniguard.trackable.utils


import android.device.DeviceManager
import android.text.TextUtils
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object OtgUtils {
    private val TAG: String = OtgUtils::class.java.simpleName
    private val mDeviceManager: DeviceManager = DeviceManager()
    private val listEnable: MutableList<String> = ArrayList()
    private val listDisable: MutableList<String> = ArrayList()

    private const val NUM_ENABLE_SIZE = 5
    private const val NUM_DISABLE_SIZE = 4

    init {
        //TODO 已上电的标识

        listEnable.add("enable")
        listEnable.add("active")
        listEnable.add("1")
        listEnable.add("2")
        listEnable.add("3")
        //TODO 已下电的标识
        listDisable.add("disable")
        listDisable.add("suspend")
        listDisable.add("0")
        listDisable.add("6")
    }

    private const val PROJECT_SQ53 = "SQ53"
    private const val PROJECT_SQ53C = "SQ53C"
    private const val PROJECT_SQ53Q = "SQ53Q"
    private const val PROJECT_SQ53B = "SQ53B"
    private const val PROJECT_SQ53Z = "SQ53Z"
    private const val PROJECT_SQ53X = "SQ53X"
    private const val PROJECT_SQ53S = "SQ53S"
    private const val PROJECT_SQ55 = "SQ55"
    private const val PROJECT_SQ55_5G = "SQ55_5G"
    private const val PROJECT_SQ55_5G_1 = "SQ55-5G"
    private const val PROJECT_SQ81 = "SQ81"

    private const val NODE_POGO_UART = "/sys/devices/soc/c170000.serial/pogo_uart"
    private const val NODE_USB_SWITCH = "/sys/devices/virtual/Usb_switch/usbswitch/function_otg_en"
    private const val NODE_POGO_5V = "/sys/devices/soc/soc:sectrl/ugp_ctrl/gp_pogo_5v_ctrl/enable"
    private const val NODE_OTG_EN_CTRL =
        "/sys/devices/soc/soc:sectrl/ugp_ctrl/gp_otg_en_ctrl/enable"
    private const val NOde_53X = "/sys/kernel/kobject_pogo_otg_status/pogo_otg_status"
    private const val NOde_53S = "/sys/devices/virtual/pogo/pogo_pin/pogo_otg_vbus"
    private const val NODE_53B = "/sys/devices/platform/otg_typecdig/pogo_5v"

    //    private static final String NODE_53B =   "/sys/kernel/kobject_pogo_otg_status/pogo_otg5v_en";
    private const val NODE_55_5G = "/sys/devices/platform/otg_iddig/pogo_5v"
    private const val NODE_81 = "/sys/devices/platform/soc/soc:meig-gpios/meig-gpios/otg_enable"

    fun setPOGOPINEnable(enable: Boolean): Boolean {
        try {
            val mProjectName: String = mDeviceManager.getSettingProperty("pwv.project")
            val node5v: String = mDeviceManager.getSettingProperty("persist.sys.pogopin.otg5v.en")

            Log.v(
                TAG,
                "projectName:$mProjectName    enable:$enable    node5v:$node5v"
            )

            //        if (!TextUtils.isEmpty(mProjectName)){
//            return true;
//        }
            if (TextUtils.equals(mProjectName, PROJECT_SQ53Q)) { //53Q 上电
                if (isChange(NODE_POGO_UART, enable)) {
                    powerControl0X31(NODE_POGO_UART, enable)
                }
                if (isChange(NODE_POGO_5V, enable)) {
                    powerControl0X31(NODE_POGO_5V, enable)
                }
            } else if (TextUtils.equals(mProjectName, PROJECT_SQ53C)) {
                if (isChange(NODE_POGO_5V, enable)) {
                    powerControl0X31(NODE_POGO_5V, enable)
                }
                if (isChange(NODE_OTG_EN_CTRL, enable)) {
                    powerControl0X31(NODE_OTG_EN_CTRL, enable)
                }
            } else if (TextUtils.equals(mProjectName, PROJECT_SQ53S)) {
//                if (!TextUtils.isEmpty(NOde_53S) && isChange(node5v, enable)) {
                if (!TextUtils.isEmpty(node5v) && isChange(node5v, enable)) {
                    powerControl0X31(node5v, enable)
                }
            } else if (TextUtils.equals(mProjectName, PROJECT_SQ81)) {
                if (isChange(NODE_81, enable)) {
                    powerControl0X33(NODE_81, enable)
                }
            } else if (TextUtils.equals(
                    mProjectName,
                    PROJECT_SQ53
                ) || TextUtils.equals(mProjectName, PROJECT_SQ53Z)
            ) { //53 、53Z上电
                if (isChange(NODE_POGO_UART, enable)) {
                    powerControl0X31(NODE_POGO_UART, enable)
                }
                if (isChange(NODE_USB_SWITCH, enable)) {
                    powerControl0X32(NODE_USB_SWITCH, enable)
                }
            } else if (TextUtils.equals(mProjectName, PROJECT_SQ53B)) {
                if (isChange(NODE_53B, enable)) {
                    powerControl0X31(NODE_53B, enable)
                }
            } else if (TextUtils.equals(mProjectName, PROJECT_SQ55_5G) || TextUtils.equals(
                    mProjectName,
                    PROJECT_SQ55_5G_1
                )
            ) {
                if (isChange(NODE_55_5G, enable)) {
                    powerControl0X31(NODE_55_5G, enable)
                }
            } else if (!TextUtils.isEmpty(node5v)) {
                if (isChange(node5v, enable)) {
                    powerControl0X31(node5v, enable)
                }

                if (TextUtils.equals(mProjectName, PROJECT_SQ53X) || TextUtils.equals(
                        mProjectName,
                        PROJECT_SQ53B
                    )
                ) {
                    if (TextUtils.equals(mProjectName, PROJECT_SQ53X)) {
                        if (isChange(NOde_53X, enable)) {
                            if (isOtgOsVersion53X) {
                                Log.v(TAG, "53X    -->   ")
                                powerControl0X31(NOde_53X, enable)
                            }
                        }
                    } else {
                        if (isChange(NOde_53X, enable)) {
                            powerControl0X31(NOde_53X, enable)
                        }
                    }
                }
            } else { //other
                Log.v(TAG, "device model not found  : $mProjectName")
                return true
            }

            return true
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.v(TAG, "Exception:" + e.message)
            return false
        }
    }

    private val isOtgOsVersion53X: Boolean
        get() {
            //获取OS硬件版本
            try {
                val osVersion: String = DeviceManager().getSettingProperty("ro.vendor.build.id")
                val timeStr = osVersion.substring(26, 32)
                Log.v(
                    TAG,
                    "isOtgOsVersion53X()   $osVersion    -->   $timeStr"
                )
                val time = timeStr.toInt()
                return if (time >= 230220) { //53X 新OS不需上电这个节点
                    false
                } else {
                    true
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                return true
            }
        }

    @Throws(Throwable::class)
    private fun powerControl0X31(node5v: String, enable: Boolean) {
        var node_1: FileOutputStream? = null
        try {
            val open_one = byteArrayOf(0x31)
            val close = byteArrayOf(0x30)
            node_1 = FileOutputStream(node5v)
            node_1.write(if (enable) open_one else close)
            Log.v("OtgUtils", "write  success :$node5v")
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                node_1?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Throws(Throwable::class)
    private fun powerControl0X32(node5v: String, enable: Boolean) {
        var node_2: FileOutputStream? = null
        try {
            val open_two = byteArrayOf(0x32)
            val close = byteArrayOf(0x30)
            node_2 = FileOutputStream(node5v)
            node_2.write(if (enable) open_two else close)
            Log.v("OtgUtils", "write  success :$node5v")
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                node_2?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Throws(Throwable::class)
    private fun powerControl0X33(node5v: String, enable: Boolean) {
        var node_3: FileOutputStream? = null
        try {
            val open_three = byteArrayOf(0x33)
            val close = byteArrayOf(0x36)
            node_3 = FileOutputStream(node5v)
            node_3.write(if (enable) open_three else close)
            Log.v("OtgUtils", "write  success :$node5v")
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                node_3?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isChange(nodepath_pogo5ven: String, enable: Boolean): Boolean {
        try {
            if (!TextUtils.isEmpty(nodepath_pogo5ven)) {
                return true
            }

            Log.v("OtgUtils", "isChange()")

            //         String status = new DeviceManager().getSettingProperty("File-"+nodepath_pogo5ven);
//         LogUtils.v("OtgUtils","isChange() status == "+status);
            val fileInputStream = FileInputStream(nodepath_pogo5ven)
            val b = ByteArray(1024)
            var nodeStr = ""
            //开始读文件
            val len = fileInputStream.read(b)
            if (len > 0) {
                nodeStr = String(b, 0, len)
            }

            if (enable) {
                for (i in 0..<NUM_ENABLE_SIZE) {
                    if (nodeStr.contains(listEnable[i])) {
                        Log.v(
                            "OtgUtils",
                            "isChange()  already  enable     $nodepath_pogo5ven     [$nodeStr]"
                        )
                        return false //已经包含，说明已经上电，不用再上电
                    }
                }
                Log.v(
                    "OtgUtils",
                    "isChange()  not   enable     $nodepath_pogo5ven     [$nodeStr]"
                )
                return true //没有包含，说明没有上电，需要上电
            } else {
                for (i in 0..<NUM_DISABLE_SIZE) {
                    if (nodeStr.contains(listDisable[i])) {
                        Log.v(
                            "OtgUtils",
                            "isChange()  already  disable     $nodepath_pogo5ven     [$nodeStr]"
                        )
                        return false //已经包含，说明已经下电，不用再下电
                    }
                }
                Log.v(
                    "OtgUtils",
                    "isChange()  not  disable     $nodepath_pogo5ven     [$nodeStr]"
                )
                return true //没有包含，说明没有上电，需要上电
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("OtgUtils", "isChange() " + nodepath_pogo5ven + "   Exception:" + e.message)
        }
        return true
    }
}

