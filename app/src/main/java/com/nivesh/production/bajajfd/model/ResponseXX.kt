package com.nivesh.production.bajajfd.model

data class ResponseXX(
    val Errors: List<Errors>,
    val MaturityAmount: Double,
    val Message: String,
    val Status: String,
    val StatusCode: Int
)