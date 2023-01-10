package com.nivesh.production.bajajfd.model

data class DocumentUpload(
    var Description: String? = null,
    var DocumentType: String? = null,
    var FDProvider: String? = null,
    var ImageEncodeToBase64: String? = null,
    var NiveshClientCode: String? = null,
    var UniqueId: String? = null
)