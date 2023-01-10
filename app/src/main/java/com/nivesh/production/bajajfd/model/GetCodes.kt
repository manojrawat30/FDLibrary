package com.nivesh.production.bajajfd.model

data class GetCodes(
    val Label: String,
    var Value: String,
    var isSelected : Boolean
) {
    override fun toString(): String {
        return Label
    }
}