package com.nivesh.production.bajajfd.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nivesh.production.bajajfd.model.*
import com.nivesh.production.bajajfd.repositories.MainRepository
import com.nivesh.production.bajajfd.util.Common
import com.nivesh.production.bajajfd.util.Common.Companion.handleError
import com.nivesh.production.bajajfd.util.Common.Companion.handleResponse
import com.nivesh.production.bajajfd.util.Constants
import com.nivesh.production.bajajfd.util.Resource
import kotlinx.coroutines.launch

open class BajajFDViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val getStepsCountMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun getStepsCount(
        requestBody: FDStepsCountRequest,
        token: String,
        activity: Activity
    ) = viewModelScope.launch(handleError(activity)) {
        if (Common.isNetworkAvailable(activity)) {
            getStepsCountMutableData.postValue(Resource.Loading())
            val response = mainRepository.getStepsCountResponse(requestBody, token)
            getStepsCountMutableData.postValue(handleResponse(response))
        }
    }

    val getClientDetailsMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun getClientDetails(getClientDetailsRequest: getClientDetailsRequest, token: String, activity: Activity) =
        viewModelScope.launch(handleError(activity)) {
            if (Common.isNetworkAvailable(activity)) {
                getClientDetailsMutableData.postValue(Resource.Loading())
                val response =
                    mainRepository.getClientDetailsResponse(getClientDetailsRequest, token)
                getClientDetailsMutableData.postValue(handleResponse(response))
            }
        }

    val getFDKYCMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun checkFDKYC(requestBody: CheckFDKYCRequest, token : String, activity: Activity) = viewModelScope.launch(handleError(activity)) {
        if (Common.isNetworkAvailable(activity)) {
            getFDKYCMutableData.postValue(Resource.Loading())
            val response = mainRepository.checkFDKYCRequest(requestBody, token)
            getFDKYCMutableData.postValue(handleResponse(response))
        }
    }

    val getPaymentReQueryMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun getPaymentReQuery(requestBody: PaymentReQueryRequest, token : String, activity: Activity) = viewModelScope.launch(handleError(activity)) {
        if (Common.isNetworkAvailable(activity)) {
            getPaymentReQueryMutableData.postValue(Resource.Loading())
            val response = mainRepository.paymentReQueryResponse(requestBody, token)
            getPaymentReQueryMutableData.postValue(handleResponse(response))
        }
    }


    // Step 1 Api
    val getMinAmountMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun getMinAmount(requestBody: GetCodeRequest, token: String, activity: Activity) =
        viewModelScope.launch(
            handleError(activity)
        ) {
            if (Common.isNetworkAvailable(activity)) {
                getMinAmountMutableData.postValue(Resource.Loading())
                val response = mainRepository.getCodesResponse(requestBody, token)
                getMinAmountMutableData.postValue(handleResponse(response))
            }
        }

    val getMaxAmountMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun getMaxAmount(requestBody: GetCodeRequest, token: String, activity: Activity) =
        viewModelScope.launch(
            handleError(activity)
        ) {
            if (Common.isNetworkAvailable(activity)) {
                getMaxAmountMutableData.postValue(Resource.Loading())
                val response = mainRepository.getCodesResponse(requestBody, token)
                getMaxAmountMutableData.postValue(handleResponse(response))
            }
        }

    val getMaturityAmountMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun getMaturityAmount(requestBody: GetMaturityAmountRequest, activity: Activity) =
        viewModelScope.launch(
            handleError(activity)
        ) {
            if (Common.isNetworkAvailable(activity)) {
                getMaturityAmountMutableData.postValue(Resource.Loading())
                val response = mainRepository.createCalculateFDMaturityAmount(requestBody,
                    Constants.token
                )
                getMaturityAmountMutableData.postValue(handleResponse(response))
            }
        }

    val getRatesMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun getRates(getRatesRequest: GetRatesRequest, token: String, activity: Activity) =
        viewModelScope.launch(
            handleError(activity)
        ) {
            if (Common.isNetworkAvailable(activity)) {
                getRatesMutableData.postValue(Resource.Loading())
                val response = mainRepository.getRatesResponse(getRatesRequest, token)
                getRatesMutableData.postValue(handleResponse(response))
            }
        }

    val getCodeMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun getCode(requestBody: GetCodeRequest, token: String, activity: Activity) =
        viewModelScope.launch(
            handleError(activity)
        ) {
            if (Common.isNetworkAvailable(activity)) {
                getCodeMutableData.postValue(Resource.Loading())
                val response = mainRepository.getCodesResponse(requestBody, token)
                getCodeMutableData.postValue(handleResponse(response))
            }
        }


    // Step 2
    val getPanCheckMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun panCheck(panCheck: PanCheckRequest, token: String, activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getPanCheckMutableData.postValue(Resource.Loading())
            val response = mainRepository.panCheck(panCheck, token)
            getPanCheckMutableData.postValue(handleResponse(response))
        }
    }

    val getTitleMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun titleApi(getCodeRequest: GetCodeRequest, token: String, activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getTitleMutableData.postValue(Resource.Loading())
            val response = mainRepository.titleCheck(getCodeRequest, token)
            getTitleMutableData.postValue(handleResponse(response))
        }
    }

    val getGenderMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun genderApi(getCodeRequest: GetCodeRequest, token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getGenderMutableData.postValue(Resource.Loading())
            val response = mainRepository.genderCheck(getCodeRequest, token)
            getGenderMutableData.postValue(handleResponse(response))
        }
    }

    val getAnnualIncomeMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun annualIncomeApi(getCodeRequest: GetCodeRequest, token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getAnnualIncomeMutableData.postValue(Resource.Loading())
            val response = mainRepository.annualIncomeCheck(getCodeRequest, token)
            getAnnualIncomeMutableData.postValue(handleResponse(response))
        }
    }

    val getRelationShipMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun relationShipApi(getCodeRequest: GetCodeRequest, token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getRelationShipMutableData.postValue(Resource.Loading())
            val response = mainRepository.relationShipCheck(getCodeRequest, token)
            getRelationShipMutableData.postValue(handleResponse(response))
        }
    }

    val getMaritalStatusMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun maritalStatusApi(getCodeRequest: GetCodeRequest, token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getMaritalStatusMutableData.postValue(Resource.Loading())
            val response = mainRepository.maritalStatusCheck(getCodeRequest, token)
            getMaritalStatusMutableData.postValue(handleResponse(response))
        }
    }

    val getOccupationMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun occupationApi(getCodeRequest: GetCodeRequest, token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getOccupationMutableData.postValue(Resource.Loading())
            val response = mainRepository.occupationCheck(getCodeRequest, token)
            getOccupationMutableData.postValue(handleResponse(response))
        }
    }


    val getStateMasterMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun stateApi(token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getStateMasterMutableData.postValue(Resource.Loading())
            val response = mainRepository.stateCheck(token)
            getStateMasterMutableData.postValue(handleResponse(response))
        }
    }

    val getCityListMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun cityListApi(cityRequest: CityRequest, token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getCityListMutableData.postValue(Resource.Loading())
            val response = mainRepository.cityCheck(cityRequest, token)
            getCityListMutableData.postValue(handleResponse(response))
        }
    }

    val getFDBankListMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun bankListApi( token: String,language: String, activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getFDBankListMutableData.postValue(Resource.Loading())
            val response = mainRepository.bankListCheck(token, language)
            getFDBankListMutableData.postValue(handleResponse(response))
        }
    }

    val getIfscCodeCheckMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun ifscCodeApi(ifsc : String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getIfscCodeCheckMutableData.postValue(Resource.Loading())
            val response = mainRepository.ifscCodeCheck(ifsc)
            getIfscCodeCheckMutableData.postValue(handleResponse(response))
        }
    }

    val getIfscCodeDetailsCheckMutableData: MutableLiveData<Resource<String>> = MutableLiveData()
    fun ifscCodeDetailsApi(ifsc : String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getIfscCodeDetailsCheckMutableData.postValue(Resource.Loading())
            val response = mainRepository.ifscCodeBankDetailsCheck(ifsc, Constants.token)
            getIfscCodeDetailsCheckMutableData.postValue(Common.handleResponse1(response))
        }
    }

    val getPaymentModeMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun paymentModeApi(getCodeRequest: GetCodeRequest, token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getPaymentModeMutableData.postValue(Resource.Loading())
            val response = mainRepository.payModeCheck(getCodeRequest, token)
            getPaymentModeMutableData.postValue(handleResponse(response))
        }
    }

    val getFDResponseMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun createFDApi(getRatesRequest: CreateFDRequest, token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getFDResponseMutableData.postValue(Resource.Loading())
            val response = mainRepository.createFDKYCResponse(getRatesRequest, token)
            getFDResponseMutableData.postValue(handleResponse(response))
        }
    }

    val bankValidationApiMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun bankValidationApi(bankValidationApiRequest : BankValidationApiRequest,token:String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            bankValidationApiMutableData.postValue(Resource.Loading())
            val response = mainRepository.bankValidationApiRequest(bankValidationApiRequest,token)
            bankValidationApiMutableData.postValue(handleResponse(response))
        }
    }

    // Step 3
    val getDocTypeMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun docTypeApi(getCodeRequest: GetCodeRequest, token: String, activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getDocTypeMutableData.postValue(Resource.Loading())
            val response = mainRepository.titleCheck(getCodeRequest, token)
            getDocTypeMutableData.postValue(handleResponse(response))
        }
    }


    val getDocumentUploadMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun documentsUpload(documentUpload: DocumentUpload, token: String, activity: Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            getDocumentUploadMutableData.postValue(Resource.Loading())
            val response = mainRepository.documentsUploadResponse(documentUpload, token)
            getDocumentUploadMutableData.postValue(handleResponse(response))
        }
    }

    // Step 4
    val getFDDetailsMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun getFDDetails(getRatesRequest: GetFDDetailsRequest, token: String,
                     activity: Activity) = viewModelScope.launch(handleError(activity)) {
        if (Common.isNetworkAvailable(activity)) {
            getFDDetailsMutableData.postValue(Resource.Loading())
            val response = mainRepository.getFDDetailsResponse(getRatesRequest, token)
            getFDDetailsMutableData.postValue(handleResponse(response))
        }
    }

    val getFDOtherMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun saveFDOtherData(getRatesRequest: SaveFDOtherDataRequest, token: String,
                        activity: Activity) = viewModelScope.launch(handleError(activity)) {
        if (Common.isNetworkAvailable(activity)) {
            getFDOtherMutableData.postValue(Resource.Loading())
            val response = mainRepository.saveFDOtherDataResponse(getRatesRequest, token)
            getFDOtherMutableData.postValue(handleResponse(response))
        }
    }

    val customerListMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun customerListApi(getCodeRequest: GetCodeRequest, token: String,activity : Activity) = viewModelScope.launch(
        handleError(activity)
    ) {
        if (Common.isNetworkAvailable(activity)) {
            customerListMutableData.postValue(Resource.Loading())
            val response = mainRepository.customerListCheck(getCodeRequest, token)
            customerListMutableData.postValue(handleResponse(response))
        }
    }

    fun updateFDPaymentStatus(getRatesRequest: GetRatesRequest, token: String,
                              activity: Activity
    ) =
        viewModelScope.launch(handleError(activity)) {
            if (Common.isNetworkAvailable(activity)) {
                getRatesMutableData.postValue(Resource.Loading())
                val response = mainRepository.updateFDPaymentStatusResponse(getRatesRequest, token)
                getRatesMutableData.postValue(handleResponse(response))
            }
        }

    val getFinalizeFDMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun finaliseFD(finalizeFDRequest: FinalizeFDRequest, token: String,
                   activity: Activity) = viewModelScope.launch(handleError(activity)) {
        if (Common.isNetworkAvailable(activity)) {
            getFinalizeFDMutableData.postValue(Resource.Loading())
            val response = mainRepository.finaliseFDResponse(finalizeFDRequest, token)
            getFinalizeFDMutableData.postValue(handleResponse(response))
        }
    }

    val getFinalizeKYCMutableData: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    fun finaliseKYC(getRatesRequest: FinalizeKYCRequest, token: String,
                    activity: Activity) = viewModelScope.launch(handleError(activity)) {
        if (Common.isNetworkAvailable(activity)) {
            getFinalizeKYCMutableData.postValue(Resource.Loading())
            val response = mainRepository.finaliseKYCResponse(getRatesRequest, token)
            getFinalizeKYCMutableData.postValue(handleResponse(response))
        }
    }

}