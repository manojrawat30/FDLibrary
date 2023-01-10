package com.nivesh.production.bajajfd.model

data class CheckFDKYCRequest(
    var DOB: String? ="",
    var Mobile: String? = "",
    var NiveshClientCode: String? = "",
    var PAN: String?= ""
)