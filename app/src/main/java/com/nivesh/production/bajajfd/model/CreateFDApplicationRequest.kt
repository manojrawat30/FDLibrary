package com.nivesh.production.bajajfd.model

data class CreateFDApplicationRequest(
    var ApplicantDetails: ApplicantDetails ? = null,
    var ApplicantRelationDetails: ApplicantRelationDetails? = null,
    var FDInvestmentDetails: FDInvestmentDetails? = null,
    var FdBankDetails: FdBankDetails? = null,
    var NomineeDetails: NomineeDetails? = null,
    var NomineeGuardianDetails: NomineeGuardianDetails ? = null
)