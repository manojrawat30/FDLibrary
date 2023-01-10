package com.nivesh.production.bajajfd.model

data class ResponseXXXXXXXX(
    val BankList: List<Bank>,
    val Errors: List<Errors>,
    val Message: String,
    val Status: String,
    val StatusCode: Int
)