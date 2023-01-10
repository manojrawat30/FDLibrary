package com.nivesh.production.bajajfd.model

import java.io.Serializable

data class BankList(
    val BankName: String?,
    val IFSCCode: String?,
    val AccountNumber: String?,
    val BranchName: String?,
    val DefaultBankFlag: String?,
    val IsValBank: String?,
    val AccountType: String?
): Serializable


