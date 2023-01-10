package com.nivesh.production.bajajfd.model

data class NomineeDetails(
    var NomineeAddress1: String?= null,
    var NomineeAddress2: String?= null,
    var NomineeAddress3: String?= null,
    var NomineeCity: String?= null,
    var NomineeCountry: String?= null,
    var NomineeDOB: String?= null,
    var NomineeFirstName: String?= null,
    var NomineeGender: String?= null,
    var NomineeLastName: String?= null,
    var NomineeMiddleName: String?= null,
    var NomineePincode: Int?= 0,
    var NomineeRelation: String?= null,
    var NomineeSalutation: String?= null,
    var NomineeState: String?= null
)