package com.nivesh.production.bajajfd.model

data class NomineeGuardianDetails(
    var GuardianAddress1: String?= null,
    var GuardianAddress2: String?= null,
    var GuardianAddress3: String?= null,
    var GuardianAge: Int?= 0,
    var GuardianCity: String?= null,
    var GuardianCountry: String?= null,
    var GuardianName: String?= null,
    var GuardianPincode: Int?= 0,
    var GuardianSalutation: String?= null,
    var GuardianState: String?= null
)