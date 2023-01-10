package com.nivesh.production.bajajfd.model

data class GetMaturityAmountRequest(
    var FDAmount: Int? = 0,
    var FDProvider: String? = "",
    var Frequency: String? = "",
    var Interest: Double? = 0.0,
    var Tenure: Int? = 0
)