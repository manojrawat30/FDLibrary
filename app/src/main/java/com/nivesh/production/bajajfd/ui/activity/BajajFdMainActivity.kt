package com.nivesh.production.bajajfd.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.adapter.DisableAdapter
import com.nivesh.production.bajajfd.adapter.SectionsPagerAdapter
import com.nivesh.production.bajajfd.api.ApiClient
import com.nivesh.production.bajajfd.databinding.ActivityBajajFdBinding
import com.nivesh.production.bajajfd.model.*
import com.nivesh.production.bajajfd.repositories.MainRepository
import com.nivesh.production.bajajfd.ui.fragment.*
import com.nivesh.production.bajajfd.ui.providerfactory.*
import com.nivesh.production.bajajfd.util.Common
import com.nivesh.production.bajajfd.util.Common.Companion.defaultShape
import com.nivesh.production.bajajfd.util.Common.Companion.getDate
import com.nivesh.production.bajajfd.util.Common.Companion.selectedShape
import com.nivesh.production.bajajfd.util.Common.Companion.showDialogValidation
import com.nivesh.production.bajajfd.util.Constants.Companion.token
import com.nivesh.production.bajajfd.util.ProgressUtil
import com.nivesh.production.bajajfd.util.ProgressUtil.hideLoading
import com.nivesh.production.bajajfd.util.ProgressUtil.showLoading
import com.nivesh.production.bajajfd.util.Resource
import com.nivesh.production.bajajfd.viewModel.*


class BajajFdMainActivity : BaseActivity() {
    lateinit var binding: ActivityBajajFdBinding
    lateinit var viewModel: BajajFDViewModel

    private val stepOneBajajFDFragment = StepOneBajajFDFragment()
    private val stepTwoBajajFDFragment = StepTwoBajajFDFragment()
    private val stepThreeBajajFDFragment = StepThreeBajajFDFragment()
    private val stepFourBajajFDFragment = StepFourBajajFDFragment()
    private val stepFiveBajajFDFragment = StepFiveBajajFDFragment()

    var createFDRequest: CreateFDRequest = CreateFDRequest()
    var createFDApplicantRequest: CreateFDApplicationRequest = CreateFDApplicationRequest()
    var applicantDetails: ApplicantDetails = ApplicantDetails()
    var fdInvestmentDetails: FDInvestmentDetails = FDInvestmentDetails()
    var applicantRelationDetails: ApplicantRelationDetails = ApplicantRelationDetails()
    var fdBankDetails: FdBankDetails = FdBankDetails()
    var nomineeDetails: NomineeDetails = NomineeDetails()
    var nomineeGuardianDetails: NomineeGuardianDetails = NomineeGuardianDetails()
    var getClientDetailsResponse: getClientDetailsResponse = getClientDetailsResponse()
    var uniqueId: String = ""
    var stepCount: Int = 0
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var fragments: Array<Fragment>
    var dialogWebView: Dialog? = null
    var loginRole: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        //start Repository
        viewModel = ViewModelProvider(
            this@BajajFdMainActivity,
            FDModelProviderFactory(MainRepository(ApiClient.getApiClient))
        )[BajajFDViewModel::class.java]
        binding = ActivityBajajFdBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(this.root)
        }

        loginRole = 5
        if (Common.isNetworkAvailable(this)) {
            getStepsCountApi()
        }

        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun getStepsCountApi() {
        if (Common.isNetworkAvailable(this)) {
            val fdStepsCount = FDStepsCountRequest()
            fdStepsCount.FDProvider = getString(R.string.bajaj)
            fdStepsCount.NiveshClientCode = "8872" // 60476
            viewModel.getStepsCount(fdStepsCount, token, this)
            viewModel.getStepsCountMutableData.observe(this) { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.e("response", "-->${response.data.toString()}")
                        val stepsCountResponse: StepsCountResponse =
                            Gson().fromJson(
                                response.data?.toString(),
                                StepsCountResponse::class.java
                            )
                        stepsCountResponse.Response.StatusCode.let { code ->
                            when (code) {
                                200 -> {
                                    stepCount = stepsCountResponse.Response.StepsCount
                                    if (stepCount == 3) {
                                        binding.llStep4.visibility = View.GONE
                                    }
                                    getClientDetailsApi(stepsCountResponse.Response.StepsCount)
                                }
                                650 -> ""
                                else -> {
                                    showDialogValidation(
                                        this@BajajFdMainActivity,
                                        stepsCountResponse.Response.Errors[0].ErrorMessage
                                    )
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            showDialogValidation(this@BajajFdMainActivity, message)
                        }
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.DataError -> {

                    }
                }
            }
        }
    }

    private fun getClientDetailsApi(stepsCount: Int) {
        if (Common.isNetworkAvailable(this@BajajFdMainActivity)) {
            val getClientDetailsRequest = getClientDetailsRequest()
            getClientDetailsRequest.client_code = "8872"
            getClientDetailsRequest.AppOrWeb = getString(R.string.app)
            getClientDetailsRequest.sub_broker_code = "1038"
            val userRequest = UserRequest()
            userRequest.UID = 0
            userRequest.IPAddress = ""
            userRequest.Source = getString(R.string.source)
            userRequest.AppOrWeb = getString(R.string.app)
            userRequest.LoggedInRoleId = loginRole

            val deviceInfo = DeviceInfo()
            deviceInfo.device_id = ""
            deviceInfo.device_id_for_UMSId = ""
            deviceInfo.device_type = getString(R.string.app)
            deviceInfo.device_model = ""
            deviceInfo.device_token = ""
            deviceInfo.device_name = ""
            deviceInfo.app_version = ""
            deviceInfo.device_os_version = ""
            userRequest.deviceInfo = deviceInfo
            getClientDetailsRequest.UserRequest = userRequest
            Log.e("getClientDetail ", " Request -->" + Gson().toJson(getClientDetailsRequest))
            showLoading(this@BajajFdMainActivity)
            viewModel.getClientDetails(getClientDetailsRequest, token, this)
            viewModel.getClientDetailsMutableData.observe(this) { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.e("getClientDetail ", " response -->${response.data.toString()}")
                        getClientDetailsResponse =
                            Gson().fromJson(
                                response.data?.toString(),
                                getClientDetailsResponse::class.java
                            )
                        getClientDetailsResponse.response?.status_code.let { code ->
                            when (code) {
                                200 -> {
                                    setViewPager(stepsCount)
                                    checkFDCKYCApi()
                                }
                                //  650 -> refreshToken()
                                else -> {
                                    showDialogValidation(this@BajajFdMainActivity, response.message)
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            showDialogValidation(this@BajajFdMainActivity, message)
                        }
                    }
                    is Resource.Loading -> {
                        hideLoading()
                    }
                    is Resource.DataError -> {

                    }
                }
            }
        }
    }

    private fun setViewPager(stepsCount: Int) {
        // steps setting
        setBackground(selectedShape(), defaultShape(), defaultShape(), defaultShape(), stepsCount)

        if (stepCount == 3) {
            fragments = arrayOf(
                stepOneBajajFDFragment,
                stepTwoBajajFDFragment,
                stepFourBajajFDFragment,
                stepFiveBajajFDFragment
            )
        } else if (stepCount == 4) {
            fragments = arrayOf(
                stepOneBajajFDFragment,
                stepTwoBajajFDFragment,
                stepThreeBajajFDFragment,
                stepFourBajajFDFragment,
                stepFiveBajajFDFragment
            )
        }

        // set viewPager
        sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, fragments)
        val viewPager: DisableAdapter = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        viewPager.setPagingEnabled(false)
        if (sectionsPagerAdapter.count > 1) {
            viewPager.offscreenPageLimit = stepCount
        } else {
            viewPager.offscreenPageLimit = 1
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        setBackground(
                            defaultShape(),
                            defaultShape(),
                            defaultShape(),
                            defaultShape(),
                            stepsCount
                        )
                    }
                    1 -> {
                        setBackground(
                            selectedShape(),
                            defaultShape(),
                            defaultShape(),
                            defaultShape(),
                            stepsCount
                        )
                    }
                    2 -> {
                        setBackground(
                            selectedShape(),
                            selectedShape(),
                            defaultShape(),
                            defaultShape(),
                            stepsCount
                        )
                    }
                    3 -> {
                        setBackground(
                            selectedShape(),
                            selectedShape(),
                            selectedShape(),
                            defaultShape(),
                            stepsCount
                        )
                    }
                    4 -> {
                        setBackground(
                            selectedShape(),
                            selectedShape(),
                            selectedShape(),
                            selectedShape(),
                            stepsCount
                        )
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    private fun checkFDCKYCApi() {
        if (Common.isNetworkAvailable(this@BajajFdMainActivity)) {
            if (getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CM_MOBILE?.isNotEmpty()!! && getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_DOB?.isNotEmpty()!! && getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_PAN?.isNotEmpty()!! && getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE?.isNotEmpty()!!) {
                val checkFDKYCRequest = CheckFDKYCRequest()
                checkFDKYCRequest.Mobile =
                    getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CM_MOBILE
                checkFDKYCRequest.DOB =
                    getDate(getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_DOB!!)
                checkFDKYCRequest.PAN =
                    getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_PAN
                checkFDKYCRequest.NiveshClientCode =
                    getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE.toString()
                viewModel.checkFDKYC(checkFDKYCRequest, token, this)
                viewModel.getFDKYCMutableData.observe(this) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Log.e("response", "-->${response.data.toString()}")
                            val getCodeResponse: GetCodeResponse =
                                Gson().fromJson(
                                    response.data?.toString(),
                                    GetCodeResponse::class.java
                                )
                            getCodeResponse.Response.StatusCode.let { code ->
                                when (code) {
                                    200 -> {

                                        fdInvestmentDetails.CustomerType = ""
                                    }
                                    //  650 -> refreshToken()
                                    else -> {
                                        showDialogValidation(
                                            this@BajajFdMainActivity,
                                            getCodeResponse.Response.Errors[0].ErrorMessage
                                        )
                                    }
                                }
                            }
                        }

                        is Resource.Error -> {
                            response.message?.let { message ->
                                showDialogValidation(this@BajajFdMainActivity, message)
                            }
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.DataError -> {

                        }
                    }
                }
            }
        }

    }

    // set background for selected/ default step
    private fun setBackground(
        drawable: Drawable?,
        drawable1: Drawable?,
        drawable2: Drawable?,
        drawable3: Drawable?,
        stepsCount: Int
    ) {
        binding.stepOne.background = drawable
        binding.stepTwo.background = drawable1
        binding.stepThree.background = drawable2
        if (stepsCount == 4) {
            binding.stepFour.background = drawable3
        }

    }

    // step 1 response
    fun stepOneApi() {
        binding.viewPager.currentItem = 1
    }

    // step 2 response
    fun stepTwoApi() {
        binding.viewPager.currentItem = 2
        if (stepCount == 3) {
            stepFourBajajFDFragment.displayReceivedData()
        }
    }

    // step 3 response
    fun stepThreeApi() {
        binding.viewPager.currentItem = 3
        stepFourBajajFDFragment.displayReceivedData()
    }

    // step 4 response
    fun stepFourApi(payUrl: String, value: String) {
        paymentDialog(payUrl, value)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun paymentDialog(payUrl: String, value: String) {
        Log.e("payUrl", "-->$payUrl")
        Log.e("value", "-->$value")
        dialogWebView = Dialog(this@BajajFdMainActivity)
        dialogWebView!!.setContentView(R.layout.row_fd_pay)
        dialogWebView!!.setCancelable(true)
        val tvCancel = dialogWebView!!.findViewById<TextView>(R.id.tvCancel)
        tvCancel.setOnClickListener {
            dialogWebView!!.dismiss()
        }
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogWebView!!.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialogWebView!!.window?.attributes = lp
        val wVPay = dialogWebView!!.findViewById<WebView>(R.id.wVPay)
        wVPay.settings.javaScriptEnabled = true
        wVPay.settings.domStorageEnabled = true
        wVPay.loadData(
            "<form name=\"frm\" action=\"$payUrl\" method=\"post\"> \n" + " <input type=\"hidden\" name=\"msg\" value=\"$value\"> \n" + " </form> \n" +
                    "<script type=\"text/javascript\"> \n" + "document.forms[\"frm\"].submit(); \n" + "</script>",
            "text/html",
            "UTF-8"
        )
        wVPay.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.e("onPageStarted", "-->$url")
                if (url.isNotEmpty() && url.contains("https://uat.nivesh.com/bajajFD/OrderStatus")) {
                    if (dialogWebView != null && dialogWebView!!.isShowing) {
                        dialogWebView!!.dismiss()
                        paymentReQueryApi()
                    }
                }
            }
        }
        dialogWebView!!.show()
    }

    fun paymentReQueryApi() {
        if (Common.isNetworkAvailable(this)) {
            val paymentReQueryRequest = PaymentReQueryRequest()
            paymentReQueryRequest.UniqueId = uniqueId
            paymentReQueryRequest.NiveshClientCode =
                getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE
            ProgressUtil.showLoading(this@BajajFdMainActivity)
            viewModel.getPaymentReQuery(paymentReQueryRequest, token, this)
            viewModel.getPaymentReQueryMutableData.observe(this) { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.e("paymentReQueryApi ", "response -->$response")
                        val paymentReQueryResponse: PaymentReQueryResponse =
                            Gson().fromJson(
                                response.data?.toString(),
                                PaymentReQueryResponse::class.java
                            )
                        paymentReQueryResponse.Response.StatusCode.let { code ->
                            when (code) {
                                650 -> ""
                                else -> {
                                    if (stepCount == 4) {
                                        binding.viewPager.currentItem = 4
                                    } else {
                                        binding.viewPager.currentItem = 3
                                    }
                                    stepFiveBajajFDFragment.getData(paymentReQueryResponse)
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            showDialogValidation(this@BajajFdMainActivity, message)
                        }
                    }
                    is Resource.Loading -> {
                        hideLoading()
                    }
                    is Resource.DataError -> {

                    }
                }
            }
        }
    }

}