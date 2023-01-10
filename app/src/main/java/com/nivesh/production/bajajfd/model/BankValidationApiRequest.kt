package com.nivesh.production.bajajfd.model

data class BankValidationApiRequest(
    var BankAccountNo: String?="",
    var BankNo: Int?= 0,
    var IFSC: String?= "",
    var Name: String? = "",
    var PhoneNo: String? = "",
    var RoleId: Int? = 0
)