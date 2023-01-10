package com.nivesh.production.bajajfd.ui.fragment

import GetRatesResponse
import ROIDataList
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.gson.Gson
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.adapter.HorizontalRecyclerViewAdapter
import com.nivesh.production.bajajfd.databinding.FragmentBajajfdStepOneBinding
import com.nivesh.production.bajajfd.model.*
import com.nivesh.production.bajajfd.ui.activity.BajajFdMainActivity
import com.nivesh.production.bajajfd.util.Common
import com.nivesh.production.bajajfd.util.Common.Companion.commonErrorMethod
import com.nivesh.production.bajajfd.util.Common.Companion.removeError
import com.nivesh.production.bajajfd.util.Constants.Companion.token
import com.nivesh.production.bajajfd.util.Resource


class StepOneBajajFDFragment : Fragment() {
    private var _binding: FragmentBajajfdStepOneBinding? = null
    private val binding get() = _binding!!

    private lateinit var rgMaturity: RadioButton
    private lateinit var listOfTenure: MutableList<ROIDataList>
    private lateinit var recyclerViewDropDownAdapter: HorizontalRecyclerViewAdapter
    private lateinit var listOfMinAmount: List<GetCodes>
    private lateinit var listOfMaxAmount: List<GetCodes>
    private lateinit var listOfFrequency: List<GetCodes>

    private var tenure: Int = 0
    private var interest: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBajajfdStepOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOfTenure = ArrayList()
        listOfMinAmount = ArrayList()
        listOfMaxAmount = ArrayList()
        listOfFrequency = ArrayList()

        rgMaturity = RadioButton(activity as BajajFdMainActivity)
        binding.edtAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8)) // upto 1 Cr
        // Amount
        binding.edtAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlDepositAmount)
                if (s.toString().trim().length >= 4) {
                    maturityAmountApi(tenure, interest)
                }
            }
        })

        // Frequency
        binding.spInterestPayout.setOnItemClickListener { _, _, position, _ ->
            removeError(binding.tlInterestPayout)
            if (listOfFrequency.isNotEmpty()) {
                binding.tvFrequency.text = listOfFrequency[position].Value
                if (!binding.tvFrequency.text.equals(getString(R.string.cumulativeText))) {
                    binding.txtCumulativeNon.text = getString(R.string.nonCumulativeROI)
                } else {
                    binding.txtCumulativeNon.text = getString(R.string.cumulativeROI)
                }
                if (binding.edtAmount.text.toString().trim().isNotEmpty()) {
                    maturityAmountApi(tenure, interest)
                }
            }
        }

        // Tenure
        binding.spTenure.setOnItemClickListener { _, _, position, _ ->
            removeError(binding.tlInterestTenure)
            if (listOfTenure.isNotEmpty()) {
                tenure = listOfTenure[position].Tenure.toInt()
                interest = listOfTenure[position].ROI
                binding.tvROI.text = interest.toString().plus(" %")
                maturityAmountApi(tenure, interest)
                recyclerViewDropDownAdapter.refresh()
            }
        }

        // Senior / Non Senior Citizen
        binding.swSeniorCitizen.setOnCheckedChangeListener { _, _ ->
            getRatesApi()
        }

        // Maturity Options
        rgMaturity.text = getString(R.string.additionalDetailOne)
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            rgMaturity = group.findViewById(checkedId)
            Log.e("Maturity", "-->" + rgMaturity.text)
        }

        // Next Button
        binding.btnNext.setOnClickListener {
            if (validation()) {
                (activity as BajajFdMainActivity).fdInvestmentDetails.FDAmount =
                    binding.edtAmount.text.toString().toDouble()
                (activity as BajajFdMainActivity).fdInvestmentDetails.Frequency =
                    binding.spInterestPayout.text.toString()
                (activity as BajajFdMainActivity).fdInvestmentDetails.Tenure = tenure
                (activity as BajajFdMainActivity).fdInvestmentDetails.Interest = interest
                (activity as BajajFdMainActivity).fdInvestmentDetails.NiveshClientCode =
                    (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE
                (activity as BajajFdMainActivity).fdInvestmentDetails.Provider =
                    getString(R.string.bajaj)
                (activity as BajajFdMainActivity).fdInvestmentDetails.IPAddress = "192.168.1.23"
                (activity as BajajFdMainActivity).fdInvestmentDetails.Device =
                    getString(R.string.app)
                (activity as BajajFdMainActivity).fdInvestmentDetails.Source =
                    getString(R.string.source)
                if (binding.swSeniorCitizen.isChecked) {
                    (activity as BajajFdMainActivity).fdInvestmentDetails.CitizenType =
                        getString(R.string.seniorCitizen)
                } else {
                    (activity as BajajFdMainActivity).fdInvestmentDetails.CitizenType =
                        getString(R.string.nonSeniorCitizen)
                }
                (activity as BajajFdMainActivity).fdInvestmentDetails.CustomerType = ""
                (activity as BajajFdMainActivity).fdInvestmentDetails.CKYCNumber = ""
                (activity as BajajFdMainActivity).fdInvestmentDetails.UniqueId =
                    (activity as BajajFdMainActivity).uniqueId

                (activity as BajajFdMainActivity).createFDApplicantRequest.FDInvestmentDetails =
                    (activity as BajajFdMainActivity).fdInvestmentDetails
                Log.e(
                    "StepOneData",
                    "-->" + Gson().toJson((activity as BajajFdMainActivity).fdInvestmentDetails)
                )
                (activity as BajajFdMainActivity).stepOneApi()
            }
        }

        minAmountApi()
        interestPayoutApi()
    }

    private fun interestPayoutApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.category)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.getCode(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getCodeMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("interestPayoutApi", " response -->${response.data.toString()}")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfFrequency = getCodeResponse.Response.GetCodesList
                                if (listOfFrequency.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfFrequency
                                    )
                                    binding.spInterestPayout.setAdapter(adapter)
                                    binding.spInterestPayout.setText(
                                        adapter.getItem(listOfFrequency.size - 1)?.Value,
                                        false
                                    )
                                    binding.tvFrequency.text =
                                        adapter.getItem(listOfFrequency.size - 1)?.Value
                                    getRatesApi()
                                }else{
                                    Common.showDialogValidation(
                                        activity as BajajFdMainActivity,
                                        "Interest Payout Frequency Data Is Missing."
                                    )
                                }
                            }
                            650 -> ""
                            else -> {
                                Common.showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        Common.showDialogValidation(activity as BajajFdMainActivity, message)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.DataError -> {

                }
            }
        }
    }

    private fun minAmountApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.minAmountCategory)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.getMinAmount(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getMinAmountMutableData.observe(
            viewLifecycleOwner
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("minAmountApi ", " response-->${response.data.toString()}")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfMinAmount = getCodeResponse.Response.GetCodesList
                                if (listOfMinAmount.isNotEmpty()) {
                                    binding.txtMinAmount.text = getString(R.string.minAmount).plus(
                                        listOfMinAmount[0].Value
                                    )
                                }
                                maxAmountApi()
                            }
                              650 -> ""
                            else -> {
                                Common.showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        Common.showDialogValidation(activity as BajajFdMainActivity, message)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.DataError -> {

                }
            }
        }
    }

    private fun maxAmountApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.MaxAmountCategory)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.getMaxAmount(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getMaxAmountMutableData.observe(
            viewLifecycleOwner
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("maxAmountApi ", " response-->${response.data.toString()}")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfMaxAmount = getCodeResponse.Response.GetCodesList
                            }
                              650 -> ""
                            else -> {
                                Common.showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        Common.showDialogValidation(activity as BajajFdMainActivity, message)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.DataError -> {

                }
            }
        }
    }

    private fun setUpRecyclerView() {

        recyclerViewDropDownAdapter = HorizontalRecyclerViewAdapter(
            activity as BajajFdMainActivity,
            listOfTenure
        ) { position ->
            tenure = listOfTenure[position].Tenure.toInt()
            interest = listOfTenure[position].ROI
            binding.tvROI.text = interest.toString().plus(" %")
            maturityAmountApi(tenure, interest)
        }
        val mLayoutManager: LayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)
        binding.rvTenure.layoutManager = mLayoutManager
        binding.rvTenure.setHasFixedSize(true)
        binding.rvTenure.itemAnimator = DefaultItemAnimator()
        binding.rvTenure.adapter = recyclerViewDropDownAdapter
    }

    private fun maturityAmountApi(tenure: Int, interest: Double) {
        if (binding.edtAmount.text.toString().length >= 4 && interest != 0.0 && tenure != 0 && binding.spInterestPayout.text.toString()
                .isNotEmpty()
        ) {
            val maturityAmountRequest = GetMaturityAmountRequest()
            maturityAmountRequest.FDProvider = getString(R.string.bajaj)
            maturityAmountRequest.FDAmount = binding.edtAmount.text.toString().toInt()
            maturityAmountRequest.Frequency = binding.spInterestPayout.text.toString()
            maturityAmountRequest.Tenure = tenure
            maturityAmountRequest.Interest = interest
            (activity as BajajFdMainActivity).viewModel.getMaturityAmount(
                maturityAmountRequest,
                activity as BajajFdMainActivity
            )
            (activity as BajajFdMainActivity).viewModel.getMaturityAmountMutableData.observe(
                viewLifecycleOwner
            ) { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.e("maturityAmountApi ", " response-->${response.data.toString()}")
                        val getMaturityAmountResponse =
                            Gson().fromJson(
                                response.data?.toString(),
                                GetCalculateMaturityAmountResponse::class.java
                            )
                        getMaturityAmountResponse.Response.StatusCode.let { code ->
                            when (code) {
                                200 -> {
                                    binding.tvMaturityAmount.text =
                                        getString(R.string.rs).plus(" ").plus(
                                            getMaturityAmountResponse.Response.MaturityAmount.toString()
                                        )
                                }
                                //   650 -> refreshToken()
                                else -> {
                                    Common.showDialogValidation(
                                        activity as BajajFdMainActivity,
                                        getMaturityAmountResponse.Response.Errors[0].ErrorMessage
                                    )
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            Common.showDialogValidation(
                                activity as BajajFdMainActivity,
                                message
                            )
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

    private fun validation(): Boolean {
        return if (binding.edtAmount.text.toString().isEmpty()) {
            commonErrorMethod(
                binding.edtAmount,
                binding.tlDepositAmount,
                getString(R.string.emptyAmount)
            )
        } else if (binding.edtAmount.text.toString()
                .toDouble() < listOfMinAmount[0].Value.toDouble()
        ) {
            commonErrorMethod(
                binding.edtAmount,
                binding.tlDepositAmount,
                getString(R.string.validMinAmount)
            )
        } else if (binding.edtAmount.text.toString()
                .toDouble() > listOfMaxAmount[0].Value.toDouble()
        ) {
            commonErrorMethod(
                binding.edtAmount,
                binding.tlDepositAmount,
                getString(R.string.validMaxAmount)
            )
        } else if (binding.spInterestPayout.text.isEmpty()) {
            Common.commonSpinnerErrorMethod(
                binding.spInterestPayout,
                binding.tlInterestPayout,
                getString(R.string.emptyInterestPayout)
            )
        } else if (binding.spTenure.text.isEmpty()) {
            Common.commonSpinnerErrorMethod(
                binding.spTenure,
                binding.tlInterestTenure,
                getString(R.string.emptyInterestTenure)
            )
        } else {
            true
        }
    }

    private fun getRatesApi() {
        val getRatesRequest = GetRatesRequest()
        getRatesRequest.fdProvider = getString(R.string.bajaj)
        getRatesRequest.frequency = binding.spInterestPayout.text.toString()
        if (binding.swSeniorCitizen.isChecked) {
            getRatesRequest.type = getString(R.string.seniorCitizen)
        } else {
            getRatesRequest.type = getString(R.string.nonSeniorCitizen)
        }
        (activity as BajajFdMainActivity).viewModel.getRates(
            getRatesRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getRatesMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    val getRatesResponse: GetRatesResponse =
                        Gson().fromJson(response.data?.toString(), GetRatesResponse::class.java)
                    getRatesResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                if (listOfTenure.isNotEmpty()) {
                                    listOfTenure.clear()
                                }
                                listOfTenure = getRatesResponse.Response.ROIDatalist
                                // Tenure
                                if (listOfTenure.isNotEmpty()) {
                                    listOfTenure.sortWith { lhs: ROIDataList, rhs: ROIDataList ->
                                        rhs.Tenure.compareTo(
                                            lhs.Tenure
                                        )
                                    }
                                    binding.ORLayout.visibility = View.VISIBLE
                                    val tenureAdapter =
                                        ArrayAdapter(
                                            activity as BajajFdMainActivity,
                                            R.layout.spinner_dropdown,
                                            listOfTenure
                                        )
                                    binding.spTenure.setAdapter(tenureAdapter)

                                    binding.spTenure.setText(
                                        tenureAdapter.getItem(0)?.Tenure.plus(
                                            " Months  |  "
                                        ).plus(tenureAdapter.getItem(0)?.ROI).plus(" %"), false
                                    )
                                    tenure = tenureAdapter.getItem(0)?.Tenure.toString().toInt()
                                    interest = tenureAdapter.getItem(0)?.ROI ?: 0.0
                                    binding.tvROI.text =
                                        tenureAdapter.getItem(0)?.ROI.toString().plus(" %")
                                    setUpRecyclerView()
                                } else {
                                    binding.ORLayout.visibility = View.GONE
                                }
                            }
                            //   650 -> refreshToken()
                            else -> {
                                Common.showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getRatesResponse.Response.Errors[0].ErrorMessage
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        Common.showDialogValidation(activity as BajajFdMainActivity, message)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.DataError -> {

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
          _binding = null
    }
}