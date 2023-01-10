package com.nivesh.production.bajajfd.interfaces

import com.google.gson.JsonObject
import com.nivesh.production.bajajfd.model.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @POST("GetRates")
    suspend fun getRates(
        @Body getRatesRequest: GetRatesRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("CheckFDCKYC")
    suspend fun checkFDKYC(
        @Body checkFDKYCRequest: CheckFDKYCRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("CreateFDApplication")
    suspend fun createFDApp(
        @Body createFDRequest: CreateFDRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("DocumentsUpload")
    suspend fun documentsUpload(
        @Body requestBody: DocumentUpload,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("SaveFDOtherData")
    suspend fun saveFDOtherData(
        @Body requestBody: SaveFDOtherDataRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("GetFDDetails")
    suspend fun getFDDetails(
        @Body requestBody: GetFDDetailsRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("UpdateFDPaymentStatus")
    suspend fun updateFDPaymentStatus(
        @Body requestBody: RequestBody,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("FinaliseFD")
    suspend fun finaliseFD(
        @Body requestBody: FinalizeFDRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("FinaliseKYC")
    suspend fun finaliseKYC(
        @Body requestBody: FinalizeKYCRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("PaymentRequery")
    suspend fun paymentReQuery(
        @Body requestBody: PaymentReQueryRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    //  @FormUrlEncoded
    @POST("GetCodes")
    suspend fun getCodes(
        @Body requestBody: GetCodeRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("CalculateFDMaturityAmount")
    suspend fun getCalculateFDMaturityAmount(
        @Body requestBody: GetMaturityAmountRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("PanCheck_S")
    suspend fun panCheckApi(
        @Body panCheck: PanCheckRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("getFDStepsCount")
    suspend fun getFDStepsCount(
        @Body fdStepsCountRequest: FDStepsCountRequest,
        @Header("token") token: String
    ):  Response<JsonObject>

    @POST("GetClientDetailV2_S")
    suspend fun getClientDetails(
        @Body getClientDetailsRequest: getClientDetailsRequest,
        @Header("token") token: String
    ): Response<JsonObject>


    @POST("GetCodes")
    suspend fun titleApi(
        @Body getCodeRequest: GetCodeRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("GetCodes")
    suspend fun genderApi(
        @Body getCodeRequest: GetCodeRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("GetCodes")
    suspend fun annualIncomeApi(
        @Body getCodeRequest: GetCodeRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("GetCodes")
    suspend fun relationShipApi(
        @Body getCodeRequest: GetCodeRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("GetCodes")
    suspend fun maritalStatusApi(
        @Body getCodeRequest: GetCodeRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("GetCodes")
    suspend fun occupationApi(
        @Body getCodeRequest: GetCodeRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("GetStateMaster")
    suspend fun stateApi(@Header("token") token: String): Response<JsonObject>

    @POST("GetCity")
    suspend fun cityApi(
        @Body cityRequest: CityRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @GET("GetFDBankList?FDProvider=Bajaj")
    suspend fun bankListApi(@Header("token") token: String, @Query("Language") language : String): Response<JsonObject>

    @GET("GetIFSC_Autofill?")
    suspend fun getIFSCApi(@Query("prefix") ifsc : String): Response<JsonObject>

    @GET("GetbankNames")
    suspend fun getIFSCBankDetailsApi(@Query( "bankname") ifsc : String, @Header("token") token: String): Response<String>

    @POST("GetCodes")
    suspend fun payModeApi(
        @Body getCodeRequest: GetCodeRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("GetCodes")
    suspend fun customerListApi(
        @Body getCodeRequest: GetCodeRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("BankValidationAPI_S")
    suspend fun bankValidationApi(
        @Body bankValidationApiRequest: BankValidationApiRequest,
        @Header("token") token: String
    ): Response<JsonObject>

    @POST("GetToken_V2")
    suspend fun freshToken(
        @Body requestBody: RequestBody
    ):Response<JsonObject>

}