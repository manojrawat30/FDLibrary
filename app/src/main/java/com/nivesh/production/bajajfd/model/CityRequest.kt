package com.nivesh.production.bajajfd.model

data class CityRequest(
    var APIName: String? = "",
    var APP_Web: String?="",
    var ClientCode: String?="",
    var HOCode: String?="",
    var RMCode: String?="",
    var RoleID: Int = 0,
    var Source: String? = "",
    var StateCode: Int? = 0,
    var Subbroker_Code: String? = "",
    var Type: String?= "",
    var UID: Int = 0
)