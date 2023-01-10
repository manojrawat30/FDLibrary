package com.nivesh.production.bajajfd.model

import com.google.gson.annotations.SerializedName

data class PanCheckRequest(
    @SerializedName("client_code")
    var clientCode: String? = null,
    @SerializedName("sub_broker_code")
    var subBrokerCode: String? = null,
    @SerializedName("pan_number")
    var panNumber: String? = null,
    @SerializedName("mobile_number")
 var mobileNumber: String? = null
)
