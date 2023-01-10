package com.nivesh.production.bajajfd.model

data class FdBankDetails(
    var AccountNumber: String?= null,
    var AccountType: String?= null,
    var BankBranch: String?= null,
    var BankName: String?= null,
    var IFSCCode: String?= null,
    var PaymentMode: String?= null
)