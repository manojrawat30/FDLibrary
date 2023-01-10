package com.nivesh.production.bajajfd.model

data class ClientDetails(
    val ARNexpiredFlag: Boolean,
    val EditProfileMessage: String,
    val IsPartiallyFilled: Boolean,
    val KYCstatus: String,
    val ProfileMessage: String,
    val ProfileStatus: String,
    val UnifiedMessage: String,
    val appliaction1_image_name: String,
    val city_of_birth: String,
    val clientMasterMFD: ClientMasterMFD,
    val country_of_birth: String,
    val created_by: String,
    val created_date: String,
    val email: String,
    val mobile: String,
    val modified_by: String,
    val modified_date: String,
    val sub_broker_code: String
)