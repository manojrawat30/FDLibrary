package com.nivesh.production.bajajfd.model

data class ResponseXXXX(
    val Errors: List<Errors>,
    val Message: String,
    val Status: String,
    val StatusCode: Int,
    val StepsCount: Int
)