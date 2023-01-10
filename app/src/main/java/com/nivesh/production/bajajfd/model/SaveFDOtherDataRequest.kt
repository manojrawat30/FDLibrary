package com.nivesh.production.bajajfd.model

data class SaveFDOtherDataRequest(
    var FDProvider: String?= "",
    var NiveshClientCode: String? = "",
    var UniqueId: String? = "",
    var Values: String? = ""
)