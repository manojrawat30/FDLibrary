package com.nivesh.production.bajajfd.model

data class DataObjectX(
    val city_id: Int,
    val city_name: String
) {
    override fun toString(): String {
        return city_name
    }
}