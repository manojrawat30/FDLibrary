package com.nivesh.production.bajajfd.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.adapter.CustomerListAdapter
import com.nivesh.production.bajajfd.databinding.FragmentBajajfdStepFourBinding
import com.nivesh.production.bajajfd.model.*
import com.nivesh.production.bajajfd.ui.activity.BajajFdMainActivity
import com.nivesh.production.bajajfd.util.Common
import com.nivesh.production.bajajfd.util.Common.Companion.showDialogValidation
import com.nivesh.production.bajajfd.util.Constants.Companion.token
import com.nivesh.production.bajajfd.util.ProgressUtil
import com.nivesh.production.bajajfd.util.Resource

class StepFourBajajFDFragment : Fragment() {

    private var _binding: FragmentBajajfdStepFourBinding? = null
    private val binding get() = _binding!!

    private lateinit var listOfCustomer: MutableList<GetCodes>
    private var selectedList: String = ""
    private var payUrl: String = ""
    private var value: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBajajfdStepFourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if ((activity as BajajFdMainActivity).loginRole == 5) {
            binding.btnNext.text = getString(R.string.pay)
            binding.btnNext.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.green,
                    null
                )
            )
        } else {
            binding.btnNext.text = getString(R.string.viewOrder)
            binding.btnNext.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.red,
                    null
                )
            )
        }

        binding.btnNext.setOnClickListener {
            selectedList = ""
            for (getCodes in listOfCustomer) {
                if (getCodes.isSelected) {
                    selectedList = if (selectedList.isEmpty()) {
                        getCodes.Value
                    } else {
                        selectedList.plus(",").plus(getCodes.Value)
                    }
                }
            }
           if (validated()) {
               if ((activity as BajajFdMainActivity).loginRole == 5) {
                   val saveFDOtherDataRequest = SaveFDOtherDataRequest()
                   saveFDOtherDataRequest.FDProvider = getString(R.string.bajaj)
                   saveFDOtherDataRequest.UniqueId = (activity as BajajFdMainActivity).uniqueId
                   saveFDOtherDataRequest.Values = selectedList
                   saveFDOtherDataRequest.NiveshClientCode =
                       (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE
                   saveFDOtherData(saveFDOtherDataRequest, payUrl, value)
               } else {
                   // go to view order
               }
           }
        }

        binding.btnBack.setOnClickListener {
            if ((activity as BajajFdMainActivity).stepCount == 4) {
                (activity as BajajFdMainActivity).binding.viewPager.currentItem = 2
            } else {
                (activity as BajajFdMainActivity).binding.viewPager.currentItem = 1
            }
        }
    }

    private fun validated(): Boolean {
        if (selectedList.isEmpty()) {
            showDialogValidation(
                activity as BajajFdMainActivity,
                getString(R.string.validTermsCondition)
            )
            return false
        } else if (!binding.checkBox.isChecked) {
            showDialogValidation(
                activity as BajajFdMainActivity,
                resources.getString(R.string.validTermsConditions)
            )
            return false
        } else {
            return true
        }
    }

    private fun saveFDOtherData(data: SaveFDOtherDataRequest, payUrl: String, value: String) {
        ProgressUtil.showLoading(activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.saveFDOtherData(
            data,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getFDOtherMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("saveFDOtherData", " response -->${response.data.toString()}")
                    val saveFDOtherDataResponse: SaveFDOtherDataResponse =
                        Gson().fromJson(
                            response.data?.toString(),
                            SaveFDOtherDataResponse::class.java
                        )
                    saveFDOtherDataResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                (activity as BajajFdMainActivity).stepFourApi(payUrl, value)
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    saveFDOtherDataResponse.Response.Errors[0].ErrorMessage
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        showDialogValidation(activity as BajajFdMainActivity, message)
                    }
                }
                is Resource.Loading -> {
                    ProgressUtil.hideLoading()
                }
                is Resource.DataError -> {

                }
            }
        }
    }

    fun displayReceivedData() {
        getFDDetailsApi()
        customerListApi()
    }

    private fun customerListApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.customerCategory)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.customerListApi(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.customerListMutableData.observe(
            viewLifecycleOwner
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("customerListApi", " response -->${response.data.toString()}")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                if (getCodeResponse.Response.GetCodesList.isNotEmpty()) {
                                    listOfCustomer = getCodeResponse.Response.GetCodesList
                                    setUpRecyclerView(listOfCustomer)
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Message
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        showDialogValidation(activity as BajajFdMainActivity, message)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.DataError -> {

                }
            }
        }
    }

    private fun setUpRecyclerView(getCustomerList: MutableList<GetCodes>) {
        binding.rvTerms.layoutManager =
            LinearLayoutManager(activity as BajajFdMainActivity)
        val customerListAdapter = CustomerListAdapter(getCustomerList)
        binding.rvTerms.adapter = customerListAdapter

    }

    private fun getFDDetailsApi() {
        if (Common.isNetworkAvailable(activity as BajajFdMainActivity)) {
            val getFDDetailsRequest = GetFDDetailsRequest()
            getFDDetailsRequest.FDProvider = getString(R.string.bajaj)
            getFDDetailsRequest.NiveshClientCode =
                (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE
            getFDDetailsRequest.UniqueId = (activity as BajajFdMainActivity).uniqueId
            ProgressUtil.showLoading(activity as BajajFdMainActivity)
            (activity as BajajFdMainActivity).viewModel.getFDDetails(
                getFDDetailsRequest,
                token,
                activity as BajajFdMainActivity
            )
            (activity as BajajFdMainActivity).viewModel.getFDDetailsMutableData.observe(
                viewLifecycleOwner
            ) { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.e("getFDDetailsApi", " response -->${response.data.toString()}")
                        val getFDDetailsResponse: GetFDDetailsResponse =
                            Gson().fromJson(
                                response.data?.toString(),
                                GetFDDetailsResponse::class.java
                            )
                        getFDDetailsResponse.Response.StatusCode.let { code ->
                            when (code) {
                                200 -> {
                                    binding.tvInvestedAmount.text =
                                        getString(R.string.rs).plus(getFDDetailsResponse.Response.FDDataResponse.FDAmount.toString())
                                    binding.tvTenure.text =
                                        getFDDetailsResponse.Response.FDDataResponse.Tenure.toString()
                                            .plus(" Months")
                                    binding.tvInterestPayout.text =
                                        getFDDetailsResponse.Response.FDDataResponse.Frequency
                                    binding.tvRateOfInterest.text =
                                        getFDDetailsResponse.Response.FDDataResponse.RateOfInterest.toString()
                                            .plus(" % p.a.")
                                    payUrl = getFDDetailsResponse.Response.FDDataResponse.PaymentUrl
                                    value = getFDDetailsResponse.Response.FDDataResponse.Value
                                }
                                //  650 -> refreshToken()
                                else -> {
                                    showDialogValidation(
                                        activity as BajajFdMainActivity,
                                        getFDDetailsResponse.Response.Errors[0].ErrorMessage
                                    )
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            showDialogValidation(activity as BajajFdMainActivity, message)
                        }
                    }
                    is Resource.Loading -> {
                        ProgressUtil.hideLoading()
                    }
                    is Resource.DataError -> {

                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}