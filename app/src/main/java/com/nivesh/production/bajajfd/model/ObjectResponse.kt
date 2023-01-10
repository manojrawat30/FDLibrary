package com.nivesh.production.bajajfd.model

data class ObjectResponse(
    val TransactionCount: Int,
    val clientDetails: ClientDetails,
    val languageid: Int,
    val membersList: List<Any>,
    val ClientBanklist : List<ClientBanklist>
)