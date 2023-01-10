package com.nivesh.production.bajajfd.model

data class UserRequest(
    var AppOrWeb: String? = "",
    var IPAddress: String? = "",
    var LoggedInRoleId: Int = 0,
    var Source: String? = "",
    var UID: Int? = 0,
    var deviceInfo: DeviceInfo? = null
)