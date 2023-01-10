package com.nivesh.production.bajajfd.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.databinding.FragmentBajajfdStepFiveBinding
import com.nivesh.production.bajajfd.model.*
import com.nivesh.production.bajajfd.ui.activity.BajajFdMainActivity
import com.nivesh.production.bajajfd.util.Common
import com.nivesh.production.bajajfd.util.Constants
import com.nivesh.production.bajajfd.util.Resource

class StepFiveBajajFDFragment : Fragment() {

    private var _binding: FragmentBajajfdStepFiveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBajajfdStepFiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCongrats.text = ""
        binding.tvSuccessMessage.text = ""
        binding.btnViewOrder.setOnClickListener {

        }
    }

    fun getData(paymentReQueryResponse: PaymentReQueryResponse) {
        if (paymentReQueryResponse.Response.StatusCode == 200) {
            if (paymentReQueryResponse.Response.Message.isNotEmpty()) {
                val arrOfStr: List<String> = paymentReQueryResponse.Response.Message.split(" ", limit = 2)
                binding.tvCongrats.text = arrOfStr[0]
                binding.tvCongrats.setTextColor(
                    ContextCompat.getColor(
                        activity as BajajFdMainActivity,
                        R.color.green
                    )
                )
                binding.tvSuccessMessage.text = arrOfStr[1]
            }
        }else{
            if (paymentReQueryResponse.Response.Message.isNotEmpty()) {
                val arrOfStr: List<String> = paymentReQueryResponse.Response.Message.split(" ", limit = 2)
                binding.tvCongrats.text = arrOfStr[0]
                binding.tvCongrats.setTextColor(
                    ContextCompat.getColor(
                        activity as BajajFdMainActivity,
                        R.color.red
                    )
                )
                binding.tvSuccessMessage.text = arrOfStr[1]
            }
        }
        finalizeFDApi()
        finalizeKYCApi()
    }

    private fun finalizeFDApi() {
        val finalizeFDRequest  = FinalizeFDRequest()
        val finaliseFD  = FinaliseFD()
        finaliseFD.FDProvider = getString(R.string.bajaj)
        finaliseFD.NiveshClientCode = (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE
        finaliseFD.UniqueId = (activity as BajajFdMainActivity).uniqueId
        finalizeFDRequest.FinaliseFD = finaliseFD

        (activity as BajajFdMainActivity).viewModel.finaliseFD(
            finalizeFDRequest, Constants.token, activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getFinalizeFDMutableData.observe(viewLifecycleOwner){ response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("finalizeKYC ", " response-->${response.data.toString()}")
                    val finalizeFDResponse: FinalizeFDResponse =
                        Gson().fromJson(response.data?.toString(), FinalizeFDResponse::class.java)
                    finalizeFDResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {

                            }
                            650 -> ""
                            else -> {
                                Common.showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    finalizeFDResponse.Response.Errors[0].ErrorMessage
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

    private fun finalizeKYCApi() {
        val finalizeKYCRequest = FinalizeKYCRequest()
        finalizeKYCRequest.FDProvider = getString(R.string.bajaj)
        finalizeKYCRequest.NiveshClientCode = (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE
        finalizeKYCRequest.UniqueId  = (activity as BajajFdMainActivity).uniqueId
        (activity as BajajFdMainActivity).viewModel.finaliseKYC(
            finalizeKYCRequest, Constants.token, activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getFinalizeKYCMutableData.observe(viewLifecycleOwner){ response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("finalizeKYC ", " response-->${response.data.toString()}")
                    val finalizeFDResponse: FinalizeFDResponse =
                        Gson().fromJson(response.data?.toString(), FinalizeFDResponse::class.java)
                    finalizeFDResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {

                            }
                            650 -> ""
                            else -> {
                                Common.showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    finalizeFDResponse.Response.Errors[0].ErrorMessage
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