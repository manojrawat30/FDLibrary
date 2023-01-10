package com.nivesh.production.bajajfd.model

data class FinalizeKYCRequest(
    var FDProvider: String? = "",
    var NiveshClientCode: String? = "",
    var UniqueId: String? = ""
)