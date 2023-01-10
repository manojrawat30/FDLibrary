package com.nivesh.production.bajajfd.model

data class FDInvestmentDetails(
    var CKYCNumber: String? = null,
    var CitizenType: String? = null,
    var CustomerType: String? = null,
    var Device: String? = null,
    var FDAmount: Double? = 0.0,
    var Frequency: String? = null,
    var IPAddress: String? = null,
    var Interest: Double? = 0.0,
    var NiveshClientCode: String? = null,
    var Provider: String? = null,
    var Source: String? = null,
    var Tenure: Int? = 0,
    var  UniqueId: String? =  ""

)