package com.nivesh.production.bajajfd.repositories

import com.nivesh.production.bajajfd.interfaces.ApiInterface
import com.nivesh.production.bajajfd.model.*

class MainRepository constructor(private val apiInterface: ApiInterface) {

    // MainActivity
    suspend fun getStepsCountResponse(requestBody: FDStepsCountRequest, token: String) =
        apiInterface.getFDStepsCount(requestBody, token)

    suspend fun getClientDetailsResponse(getClientDetails: getClientDetailsRequest, token: String) =
        apiInterface.getClientDetails(getClientDetails, token)


    // Step One
    suspend fun getRatesResponse(getRatesRequest: GetRatesRequest, token: String) =
        apiInterface.getRates(getRatesRequest, token)

    suspend fun getCodesResponse(requestBody: GetCodeRequest, token: String) =
        apiInterface.getCodes(requestBody, token)

    suspend fun createCalculateFDMaturityAmount(
        requestBody: GetMaturityAmountRequest,
        token: String
    ) =
        apiInterface.getCalculateFDMaturityAmount(requestBody, token)


    // Step Two
    suspend fun createFDKYCResponse(createFDRequest: CreateFDRequest, token: String) =
        apiInterface.createFDApp(createFDRequest, token)

    suspend fun panCheck(panCheck: PanCheckRequest, token: String) =
        apiInterface.panCheckApi(panCheck, token)

    suspend fun titleCheck(getCodeRequest: GetCodeRequest, token: String) =
        apiInterface.titleApi(getCodeRequest, token)

    suspend fun genderCheck(getCodeRequest: GetCodeRequest, token: String) =
        apiInterface.genderApi(getCodeRequest, token)

    suspend fun annualIncomeCheck(getCodeRequest: GetCodeRequest, token: String) =
        apiInterface.annualIncomeApi(getCodeRequest, token)

    suspend fun relationShipCheck(getCodeRequest: GetCodeRequest, token: String) =
        apiInterface.relationShipApi(getCodeRequest, token)

    suspend fun maritalStatusCheck(getCodeRequest: GetCodeRequest, token: String) =
        apiInterface.maritalStatusApi(getCodeRequest, token)

    suspend fun occupationCheck(getCodeRequest: GetCodeRequest, token: String) =
        apiInterface.occupationApi(getCodeRequest, token)

    suspend fun stateCheck(token: String) =
        apiInterface.stateApi(token)

    suspend fun cityCheck(cityRequest: CityRequest, token: String) =
        apiInterface.cityApi(cityRequest, token)

    suspend fun bankListCheck(token: String, language: String) =
        apiInterface.bankListApi(token, language)

    suspend fun ifscCodeCheck(str: String) =
        apiInterface.getIFSCApi(str)

    suspend fun ifscCodeBankDetailsCheck(str: String, token: String) =
        apiInterface.getIFSCBankDetailsApi(str, token)

    suspend fun payModeCheck(getCodeRequest: GetCodeRequest, token: String) =
        apiInterface.payModeApi(getCodeRequest, token)

    suspend fun bankValidationApiRequest(bankValidationApiRequest: BankValidationApiRequest, token: String) =
        apiInterface.bankValidationApi(bankValidationApiRequest, token)



    // Step Three
    suspend fun documentsUploadResponse(getRatesRequest: DocumentUpload, token: String) =
        apiInterface.documentsUpload(getRatesRequest, token)


    suspend fun saveFDOtherDataResponse(getRatesRequest: SaveFDOtherDataRequest, token: String) =
        apiInterface.saveFDOtherData(getRatesRequest, token)

    suspend fun getFDDetailsResponse(getRatesRequest: GetFDDetailsRequest, token: String) =
        apiInterface.getFDDetails(getRatesRequest, token)

    suspend fun updateFDPaymentStatusResponse(getRatesRequest: GetRatesRequest, token: String) =
        apiInterface.getRates(getRatesRequest, token)


    // Step 4
    suspend fun customerListCheck(getCodeRequest: GetCodeRequest, token: String) =
        apiInterface.customerListApi(getCodeRequest, token)

    suspend fun checkFDKYCRequest(checkFDKYCRequest: CheckFDKYCRequest, token: String) =
        apiInterface.checkFDKYC(checkFDKYCRequest, token)


    // Step 5
    suspend fun finaliseFDResponse(getRatesRequest: FinalizeFDRequest, token: String) =
        apiInterface.finaliseFD(getRatesRequest, token)

    suspend fun finaliseKYCResponse(getRatesRequest: FinalizeKYCRequest, token: String) =
        apiInterface.finaliseKYC(getRatesRequest, token)

    suspend fun paymentReQueryResponse(getRatesRequest: PaymentReQueryRequest, token: String) =
        apiInterface.paymentReQuery(getRatesRequest, token)
}