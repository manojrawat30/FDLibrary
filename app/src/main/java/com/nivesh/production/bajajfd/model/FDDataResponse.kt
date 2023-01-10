package com.nivesh.production.bajajfd.model

data class FDDataResponse(
    val FDAmount: Double,
    val Frequency: String,
    val ParameterName: String,
    val PaymentUrl: String,
    val RateOfInterest: Double,
    val Tenure: Int,
    val UniqueId: String,
    val Value: String
)