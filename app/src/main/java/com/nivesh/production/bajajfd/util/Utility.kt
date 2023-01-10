package com.nivesh.production.bajajfd.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.nivesh.production.bajajfd.model.DeviceInfo
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class Utility {

    companion object{

        fun convert_sha256(rawString: String): String {
            return try {
                val digest = MessageDigest.getInstance("SHA-256")
                var hash = ByteArray(0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    hash = digest.digest(rawString.toByteArray(StandardCharsets.UTF_8))
                }
                val hexString = StringBuilder()
                for (i in hash.indices) {
                    val hex = Integer.toHexString(0xff and hash[i].toInt())
                    if (hex.length == 1) hexString.append('0')
                    hexString.append(hex)
                }
                hexString.toString().uppercase(Locale.getDefault())
            } catch (ex: Exception) {
                throw RuntimeException(ex)
            }
        }

        fun convert_sha256_2X(rawString: String): String {
            return try {
                val digest = MessageDigest.getInstance("SHA-256")
                var hash = ByteArray(0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    hash = digest.digest(rawString.toByteArray(StandardCharsets.UTF_8))
                }
                val hexString = java.lang.StringBuilder()
                for (i in hash.indices) {
                    val hex = Integer.toHexString(0xff and hash[i].toInt())
                    if (hex.length == 1) hexString.append('0')
                    hexString.append(hex)
                }
                hexString.toString()
            } catch (ex: java.lang.Exception) {
                throw java.lang.RuntimeException(ex)
            }
        }

        @SuppressLint("HardwareIds")
        fun getDeviceInfo(mContext: Context): DeviceInfo {
            val deviceInfo = DeviceInfo()
            var device_id: String? = ""
            try {
                val telephonyManager =
                    mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                deviceInfo.app_version = (
                    SharedPrefrenceDataMethods.getVersionCode(
                        Constants.KEY_VERSION_CODE,
                        mContext
                    )
                )

                deviceInfo.device_model = (if (TextUtils.isEmpty(Build.DEVICE)) "" else Build.DEVICE)
                deviceInfo.device_os_version = (if (TextUtils.isEmpty(Build.VERSION.RELEASE)) "" else Build.VERSION.RELEASE)
                deviceInfo.device_name = (if (TextUtils.isEmpty(Build.PRODUCT)) "" else Build.PRODUCT)
                deviceInfo.device_token = (
                    if (TextUtils.isEmpty(
                            SharedPrefrenceDataMethods.getGcmAppId(
                                Constants.KEY_GCM_APP_ID,
                                mContext
                            )
                        )
                    ) "" else SharedPrefrenceDataMethods.getGcmAppId(
                        Constants.KEY_GCM_APP_ID,
                        mContext
                    )
                )
                deviceInfo.device_type = ("Android")

                // Hemant Code Added start 28-10-2020
                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        device_id = telephonyManager.imei
                        if (device_id == null) {
                            device_id =
                                if (TextUtils.isEmpty(Build.getSerial())) "" else Build.getSerial()
                        }
                    } else {
                        device_id = telephonyManager.deviceId
                        if (device_id == null) {
                            device_id =
                                if (TextUtils.isEmpty(Build.SERIAL)) "" else Build.SERIAL
                        }
                    }
                } else {
                    device_id = telephonyManager.deviceId
                    if (device_id == null) {
                        device_id = if (TextUtils.isEmpty(Build.SERIAL)) "" else Build.SERIAL
                    }
                }
                if (device_id == null || device_id.isEmpty() || device_id.equals(
                        "unknown",
                        ignoreCase = true
                    )
                ) {
                    device_id = if (TextUtils.isEmpty(
                            Settings.Secure.getString(
                                mContext.contentResolver,
                                Settings.Secure.ANDROID_ID
                            )
                        )
                    ) "" else Settings.Secure.getString(
                        mContext.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                }
                Log.e("device_id", "-> $device_id")
                // Hemant changes 07-11-2019 start commented and Added 12-12-2019
//                deviceInfo.device_id = (device_id + "#" + com.nivesh.production.util.Utility.getFlavor())
                // Hemant changes 07-11-2019 end commented  and Added 12-12-2019


                // Hemant changes 07-11-2019 start added and commented 12-12-2019
                deviceInfo.device_id_for_UMSId = (device_id)
                // deviceInfo.setDeviceId(device_id);
                // Hemant changes 07-11-2019 end added and commented 12-12-2019
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                // Hemant changes 07-11-2019 start Added 28-10-2020
                if (device_id == null || device_id.isEmpty() || device_id.equals(
                        "unknown",
                        ignoreCase = true
                    )
                ) {
                    device_id = if (TextUtils.isEmpty(
                            Settings.Secure.getString(
                                mContext.contentResolver,
                                Settings.Secure.ANDROID_ID
                            )
                        )
                    ) "" else Settings.Secure.getString(
                        mContext.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                }
                Log.e("device_id", "-> $device_id")
//                deviceInfo.setDeviceId(device_id + "#" + com.nivesh.production.util.Utility.getFlavor())
                deviceInfo.device_id_for_UMSId = (device_id)
                // Hemant changes 07-11-2019 end Added 28-10-2020
            }
            return deviceInfo
        }
    }
}