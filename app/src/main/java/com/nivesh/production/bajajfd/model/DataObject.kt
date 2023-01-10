package com.nivesh.production.bajajfd.model

import java.io.Serializable

data class DataObject(
    var BSE_State_Code: String? = "",
    var CAMS_statecode: String? = "",
    var Country_Id: Int? = 0,
    var State_Code: String?= "",
    var State_Id: Int? = 0,
    var State_Name: String? = "",
    var signzyCode: String? = ""
): Serializable {
    override fun toString(): String {
        return State_Name.toString()
    }
}