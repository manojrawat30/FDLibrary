package com.nivesh.production.bajajfd.model

data class GetFDDetailsRequest(
    var FDProvider: String? = "",
    var NiveshClientCode: String? = "",
    var UniqueId: String? = ""
)