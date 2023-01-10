package com.nivesh.production.bajajfd.model

data class getClientDetailsRequest(
    var AppOrWeb: String? = "",
    var UserRequest: UserRequest? = null,
    var client_code: String? = "",
    var sub_broker_code: String? = ""
)