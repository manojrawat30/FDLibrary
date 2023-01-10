package com.nivesh.production.bajajfd.model

import com.google.gson.annotations.SerializedName

data class GetRatesRequest(
    @SerializedName("FDProvider") var fdProvider: String? = null,
    @SerializedName("Frequency") var frequency: String? = null,
    @SerializedName("Type") var type: String? = null
)
