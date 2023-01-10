package com.nivesh.production.bajajfd.model

data class GetCodeRequest(
    var Category: String ? = null,
    var InputValue: String? = null,
    var Language: String ? = null,
    var ProductName: String? = null
)