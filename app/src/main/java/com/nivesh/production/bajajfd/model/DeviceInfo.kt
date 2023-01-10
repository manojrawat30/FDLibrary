package com.nivesh.production.bajajfd.model

data class DeviceInfo(
    var app_version: String? = "",
    var device_id: String? = "",
    var device_model: String? = "",
    var device_name: String? ="",
    var device_os_version: String? = "",
    var device_token: String? = "",
    var device_type: String? = "",
    var device_id_for_UMSId : String? = ""
)