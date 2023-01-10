package com.nivesh.production.bajajfd.model

data class ResponseX(
    val Errors: List<Errors>,
    val GetCodesList: MutableList<GetCodes>,
    val Message: String,
    val Status: String,
    val StatusCode: Int
)