package com.uniguard.trackable.data.remote.interceptor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.core.content.pm.PackageInfoCompat
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class CustomHeaderInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {
    @SuppressLint("HardwareIds")
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        requestBuilder.apply {
            addHeader("x-platform", "mobile")
            addHeader("x-app-build", versionCode)
            addHeader("x-device-name", deviceName)
            addHeader("x-device-uid", deviceId)
        }

        return chain.proceed(requestBuilder.build())
    }
}