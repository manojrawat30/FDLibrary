package com.nivesh.production.bajajfd.model

data class ResponseXXXXXXXXXXX(
    val Errors: List<Errors>,
    val FDCreationDetailsResponse: FDCreationDetailsResponse,
    val Message: String,
    val Status: String,
    val StatusCode: Int
)