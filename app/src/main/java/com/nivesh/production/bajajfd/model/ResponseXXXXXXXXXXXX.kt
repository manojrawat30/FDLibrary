package com.nivesh.production.bajajfd.model

import com.nivesh.production.bajajfd.util.Resource

data class ResponseXXXXXXXXXXXX(
    val Errors: List<Errors>,
    val FDDataResponse: FDDataResponse,
    val Message: String,
    val Status: String,
    val StatusCode: Int
)