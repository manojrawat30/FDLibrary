package com.nivesh.production.bajajfd.model

data class ClientBanklist(
    var AccountNumber: String?="",
    var AccountType: String?="",
    var BankName: String?="",
    var BranchName: String?="",
    val DefaultBankFlag: String?="",
    var IFSCCode: String?="",
    var IsValBank: Int? = 0
)