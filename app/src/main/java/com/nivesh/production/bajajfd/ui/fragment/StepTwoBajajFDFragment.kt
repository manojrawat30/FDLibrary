package com.nivesh.production.bajajfd.ui.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.adapter.BankListAdapter
import com.nivesh.production.bajajfd.adapter.PaymentModeAdapter
import com.nivesh.production.bajajfd.adapter.RecommendedBankListAdapter
import com.nivesh.production.bajajfd.databinding.FragmentBajajfdStepTwoBinding
import com.nivesh.production.bajajfd.model.*
import com.nivesh.production.bajajfd.ui.activity.BajajFdMainActivity
import com.nivesh.production.bajajfd.util.Common.Companion.commonErrorAutoCompleteMethod
import com.nivesh.production.bajajfd.util.Common.Companion.commonErrorMethod
import com.nivesh.production.bajajfd.util.Common.Companion.commonSpinnerErrorMethod
import com.nivesh.production.bajajfd.util.Common.Companion.getDate
import com.nivesh.production.bajajfd.util.Common.Companion.isIndianMobileNo
import com.nivesh.production.bajajfd.util.Common.Companion.isMinor
import com.nivesh.production.bajajfd.util.Common.Companion.isValidEmail
import com.nivesh.production.bajajfd.util.Common.Companion.isValidName
import com.nivesh.production.bajajfd.util.Common.Companion.isValidPan
import com.nivesh.production.bajajfd.util.Common.Companion.removeError
import com.nivesh.production.bajajfd.util.Common.Companion.showDialogValidation
import com.nivesh.production.bajajfd.util.Constants.Companion.token
import com.nivesh.production.bajajfd.util.ProgressUtil
import com.nivesh.production.bajajfd.util.Resource
import java.util.*


class StepTwoBajajFDFragment : Fragment() {

    private var _binding: FragmentBajajfdStepTwoBinding? = null
    private val binding get() = _binding!!

    private lateinit var listOfTitle: List<GetCodes>
    private lateinit var listOfGender: List<GetCodes>
    private lateinit var listOfAnnualIncome: List<GetCodes>
    private lateinit var listOfRelationShip: List<GetCodes>
    private lateinit var listOfMaritalStatus: List<GetCodes>
    private lateinit var listOfOccupation: List<GetCodes>
    private lateinit var listOfPayMode: List<GetCodes>
    private lateinit var listOfStates: List<DataObject>
    private lateinit var listOfCities: List<DataObjectX>
    private lateinit var listOfIFSC: MutableList<String>

    private lateinit var bankListAdapter: BankListAdapter
    private lateinit var paymentModeAdapter: PaymentModeAdapter
    private lateinit var stateObject: DataObject
    private val listOfBanks: MutableList<ClientBanklist> = mutableListOf()

    private lateinit var rbBank: RadioButton
    private lateinit var rbPaymentMode: RadioButton
    private var cal = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBajajfdStepTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listOfTitle = ArrayList()
        listOfGender = ArrayList()
        listOfAnnualIncome = ArrayList()
        listOfRelationShip = ArrayList()
        listOfMaritalStatus = ArrayList()
        listOfOccupation = ArrayList()
        listOfStates = ArrayList()
        listOfIFSC = ArrayList()
        listOfPayMode = ArrayList()
        stateObject = DataObject()
        rbPaymentMode = RadioButton(activity as BajajFdMainActivity)

        // Personal Details
        binding.edtMobileNumber.filters = arrayOf<InputFilter>(LengthFilter(10))
        binding.edtPANNumber.filters = arrayOf<InputFilter>(LengthFilter(10))
        binding.edtPinCode.filters = arrayOf<InputFilter>(LengthFilter(6))
        binding.edtIFSC.filters = arrayOf<InputFilter>(LengthFilter(11))

        binding.edtMobileNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlMobileNumber)
            }
        })
        binding.edtMobileNumber.setText((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.mobile)
        binding.edtMobileNumber.setSelection(binding.edtMobileNumber.text.toString().length)
        // create an OnDateSetListener
        if (!(activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_DOB.isNullOrEmpty()) {
            binding.edtDOB.setText(getDate((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_DOB.toString()))
        }
        binding.edtDOB.setOnClickListener {
            removeError(binding.tlDOB)
            datePicker(binding.edtDOB, 1)
        }


        binding.edtPANNumber.setText((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_PAN)
        binding.edtPANNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlPanNumber)
                if (s.toString().trim().length == 10) {
                    panCheckApi()
                }
            }
        })

        binding.spTitle.setOnItemClickListener { _, _, _, _ ->
            removeError(binding.tlTitle)
        }

        binding.edtFirstName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlFirstName)
            }
        })
        binding.edtMiddleName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlMiddleName)
            }
        })
        binding.edtLastName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlLastName)
            }
        })

        val splitStringName =
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_APPNAME1?.split(
                "\\s".toRegex()
            )?.toTypedArray()
        when (splitStringName?.size) {
            1 -> {
                binding.edtFirstName.setText(splitStringName[0])
            }
            2 -> {
                binding.edtFirstName.setText(splitStringName[0])
                binding.edtLastName.setText(splitStringName[1])
            }
            3 -> {
                binding.edtFirstName.setText(splitStringName[0])
                binding.edtMiddleName.setText(splitStringName[1])
                binding.edtLastName.setText(splitStringName[2])
            }
        }

        binding.spGender.setOnItemClickListener { _, _, _, _ ->
            removeError(binding.tlGender)
        }

        binding.edtEmail.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlEmail)
            }
        })
        binding.edtEmail.setText((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_EMAIL)

        binding.spOccupation.setOnItemClickListener { _, _, _, _ ->
            removeError(binding.tlOccupation)
        }

        binding.edtQualification.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlQualification)
            }
        })

        binding.spMarital.setOnItemClickListener { _, _, _, _ ->
            removeError(binding.tlMarital)
        }

        binding.edtAddressLine1.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlAddressLine1)
            }
        })
        binding.edtAddressLine1.setText((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_ADD1)
        binding.edtAddressLine2.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlAddressLine2)
            }
        })
        binding.edtAddressLine2.setText((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_ADD2)
        binding.edtAddressLine3.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlAddressLine3)
            }
        })
        binding.edtAddressLine3.setText((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_ADD3)

        binding.spState.setOnItemClickListener { parent, _, position, _ ->
            removeError(binding.tlState)
            stateObject = parent.getItemAtPosition(position) as DataObject
        }
        binding.spCity.setOnItemClickListener { _, _, _, _ ->
            removeError(binding.tlCity)
        }
        binding.edtPinCode.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlPinCode)
            }
        })
        binding.edtPinCode.setText((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_PINCODE)

        // Nominee Details
        binding.spNomineeTitle.setOnItemClickListener { _, _, _, _ ->
            binding.tlNomineeTitle.error = null
        }

        binding.edtNomineeFirstName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlNomineeFirstName)
            }
        })
        binding.edtNomineeMiddleName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlNomineeMiddleName)
            }
        })
        binding.edtNomineeLastName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlNomineeLastName)
            }
        })

        val splitNomineeName =
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.Nominees?.get(
                0
            )?.NomineeName?.split(
                "\\s".toRegex()
            )?.toTypedArray()
        when (splitNomineeName?.size) {
            1 -> {
                binding.edtNomineeFirstName.setText(splitNomineeName[0])
            }
            2 -> {
                binding.edtNomineeFirstName.setText(splitNomineeName[0])
                binding.edtNomineeLastName.setText(splitNomineeName[1])
            }
            3 -> {
                binding.edtNomineeFirstName.setText(splitNomineeName[0])
                binding.edtNomineeMiddleName.setText(splitNomineeName[1])
                binding.edtNomineeLastName.setText(splitNomineeName[2])
            }
        }

        if (!(activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.Nominees?.get(
                0
            )?.NomineeDOB.isNullOrEmpty()
        ) {
            binding.edtNomineeDOB.setText(
                getDate(
                    (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.Nominees?.get(
                        0
                    )?.NomineeDOB.toString()
                )
            )
        }
        binding.edtNomineeDOB.setOnClickListener {
            removeError(binding.tlNomineeDOB)
            datePicker(binding.edtNomineeDOB, 2)
        }

        binding.spNomineeRelation.setText(
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.Nominees?.get(
                0
            )?.NomineeRelationship
        )
        binding.spNomineeRelation.setOnItemClickListener { _, _, _, _ ->
            removeError(binding.tlNomineeRelation)
        }

        // Guardian Details
        binding.edtGuardianName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlGuardianName)
            }
        })
        binding.edtGuardianName.setText(
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.Nominees?.get(
                0
            )?.NomineeGuardian
        )

        binding.edtGuardianAge.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlGuardianAge)
            }
        })

        binding.spGuardianRelation.setOnItemClickListener { _, _, _, _ ->
            removeError(binding.tlGuardianRelation)
        }

        binding.edtGuardianAddress.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlGuardianAddress)
            }
        })
        binding.edtGuardianPinCode.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlGuardianPinCode)
            }
        })

        binding.rgBank.setOnCheckedChangeListener { group, checkedId ->
            rbBank = group.findViewById(checkedId)
            Log.e("BankType", "-->" + rbBank.text)
        }


        // Bank Details
        binding.edtIFSC.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlIFSC)
                ifscCodeCheckApi(s.toString())
            }
        })
        binding.edtAccountNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlAccountNumber)
            }
        })
        binding.edtBankName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlBankName)
            }
        })
        binding.edtBankBranch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeError(binding.tlBankBranchName)
            }
        })

        binding.btnAddBank.setOnClickListener {
            if (validateBank()) {
                verifyAccountNo(
                    binding.edtAccountNumber.text.toString(),
                    binding.edtIFSC.text.toString(),
                    binding.edtFirstName.text.toString().plus(" ")
                        .plus(binding.edtMiddleName.text.toString()).plus(" ")
                        .plus(binding.edtLastName.text.toString()),
                    binding.edtMobileNumber.text.toString()
                , (activity as BajajFdMainActivity).loginRole)
            }
        }

        binding.tvEligibleBankOption.setOnClickListener {
            apiForEligibleBankList()
        }

        if ((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.ClientBanklist?.isEmpty()!!) {
            binding.llBankDetails.visibility = View.VISIBLE
        } else {
            binding.llBankDetails.visibility = View.GONE
        }

        binding.addBankDetail.setOnClickListener {
            if (bankListAdapter.itemCount == 5) {
                showDialogValidation(activity as BajajFdMainActivity, getString(R.string.fiveAccountValidation))
            } else {
               showHideBankDetail()
            }
        }

        titleApi()
        setUpRecyclerView(
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.ClientBanklist,
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_ACCNO1
        )

        binding.tvPersonalDetails.setOnClickListener {
            if (binding.llPersonalDetail.visibility == View.VISIBLE) {
                binding.llPersonalDetail.visibility = View.GONE
            } else {
                binding.llPersonalDetail.visibility = View.VISIBLE
            }
        }
        binding.tvNomineeDetails.setOnClickListener {
            if (binding.llNomineeDetail.visibility == View.VISIBLE) {
                binding.llNomineeDetail.visibility = View.GONE
            } else {
                binding.llNomineeDetail.visibility = View.VISIBLE
            }
        }
        binding.tvBankDetails.setOnClickListener {
            if (binding.llBankDetails.visibility == View.VISIBLE) {
                binding.llBankDetails.visibility = View.GONE
            } else {
                binding.llBankDetails.visibility = View.VISIBLE
            }
        }

        binding.btnNext.setOnClickListener {
            if (validation()) {
                // Applicant Details
                (activity as BajajFdMainActivity).applicantDetails.ApplicantSalutation =
                    binding.spTitle.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantGender =
                    binding.spGender.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantFirstName =
                    binding.edtFirstName.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantMiddleName =
                    binding.edtMiddleName.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantLastName =
                    binding.edtLastName.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantDOB =
                    binding.edtDOB.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantPAN =
                    binding.edtPANNumber.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantMobile =
                    binding.edtMobileNumber.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantOccupation =
                    binding.spOccupation.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantQualification =
                    binding.edtQualification.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantMaritalStatus =
                    binding.spMarital.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantEmail =
                    binding.edtEmail.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantAddress1 =
                    binding.edtAddressLine1.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantAddress2 =
                    binding.edtAddressLine2.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantAddress3 =
                    binding.edtAddressLine3.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantCity =
                    binding.spCity.text.toString()
                (activity as BajajFdMainActivity).applicantDetails.ApplicantState =
                    stateObject.State_Code
                (activity as BajajFdMainActivity).applicantDetails.ApplicantCountry = "India"
                (activity as BajajFdMainActivity).applicantDetails.ApplicantPincode =
                    binding.edtPinCode.text.toString().toInt()
                (activity as BajajFdMainActivity).applicantDetails.AnnualIncome =
                    binding.spIncome.text.toString()

                // Applicant Relation Details
                (activity as BajajFdMainActivity).applicantRelationDetails.ApplicantRelation = ""
                (activity as BajajFdMainActivity).applicantRelationDetails.ApplicantRelationSalutation =
                    ""
                (activity as BajajFdMainActivity).applicantRelationDetails.ApplicantRelationFirstName =
                    ""
                (activity as BajajFdMainActivity).applicantRelationDetails.ApplicantRelationLastName =
                    ""
                (activity as BajajFdMainActivity).applicantRelationDetails.ApplicantMotherFirstName =
                    ""
                (activity as BajajFdMainActivity).applicantRelationDetails.ApplicantMotherLastName =
                    ""

                // Applicant Nominee Details
                (activity as BajajFdMainActivity).nomineeDetails.NomineeSalutation =
                    binding.spNomineeTitle.text.toString()
                (activity as BajajFdMainActivity).nomineeDetails.NomineeGender =
                    binding.spGender.text.toString()
                (activity as BajajFdMainActivity).nomineeDetails.NomineeFirstName =
                    binding.edtNomineeFirstName.text.toString()
                (activity as BajajFdMainActivity).nomineeDetails.NomineeMiddleName =
                    binding.edtNomineeMiddleName.text.toString()
                (activity as BajajFdMainActivity).nomineeDetails.NomineeLastName =
                    binding.edtNomineeLastName.text.toString()
                (activity as BajajFdMainActivity).nomineeDetails.NomineeDOB =
                    binding.edtNomineeDOB.text.toString()
                (activity as BajajFdMainActivity).nomineeDetails.NomineeRelation =
                    binding.spNomineeRelation.text.toString()
                (activity as BajajFdMainActivity).nomineeDetails.NomineeAddress1 = ""
                (activity as BajajFdMainActivity).nomineeDetails.NomineeAddress2 = ""
                (activity as BajajFdMainActivity).nomineeDetails.NomineeAddress3 = ""
                (activity as BajajFdMainActivity).nomineeDetails.NomineeCity = ""
                (activity as BajajFdMainActivity).nomineeDetails.NomineeState = ""
                (activity as BajajFdMainActivity).nomineeDetails.NomineeCountry = "India"
                (activity as BajajFdMainActivity).nomineeDetails.NomineePincode = 0

                // Applicant Nominee Guardian Details
                (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianSalutation = binding.spGuardianRelation.text.toString()
                (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianName =
                    binding.edtGuardianName.text.toString()
                if (binding.edtGuardianAge.text.toString().isEmpty()) {
                    (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianAge = 0
                } else {
                    (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianAge =
                        binding.edtGuardianAge.text.toString().toInt()
                }
                (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianCountry = "India"
                (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianCity = ""
                (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianState = ""
                if (binding.edtGuardianPinCode.text.toString().isEmpty()) {
                    (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianPincode = 0
                } else {
                    (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianPincode =
                        binding.edtGuardianPinCode.text.toString().toInt()
                }

                (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianAddress1 =
                    binding.edtGuardianAddress.text.toString()
                (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianAddress2 = ""
                (activity as BajajFdMainActivity).nomineeGuardianDetails.GuardianAddress3 = ""

                // Applicant Bank Details
                (activity as BajajFdMainActivity).fdBankDetails.AccountType =
                    bankListAdapter.getSelected()?.AccountType
                (activity as BajajFdMainActivity).fdBankDetails.BankBranch =
                    bankListAdapter.getSelected()?.BranchName
                (activity as BajajFdMainActivity).fdBankDetails.IFSCCode =
                    bankListAdapter.getSelected()?.IFSCCode
                (activity as BajajFdMainActivity).fdBankDetails.AccountNumber =
                    bankListAdapter.getSelected()?.AccountNumber
                (activity as BajajFdMainActivity).fdBankDetails.BankName =
                    bankListAdapter.getSelected()?.BankName
                (activity as BajajFdMainActivity).fdBankDetails.PaymentMode =
                    rbPaymentMode.text.toString()

                (activity as BajajFdMainActivity).createFDApplicantRequest.ApplicantDetails =
                    (activity as BajajFdMainActivity).applicantDetails
                (activity as BajajFdMainActivity).createFDApplicantRequest.ApplicantRelationDetails =
                    (activity as BajajFdMainActivity).applicantRelationDetails
                (activity as BajajFdMainActivity).createFDApplicantRequest.FdBankDetails =
                    (activity as BajajFdMainActivity).fdBankDetails
                (activity as BajajFdMainActivity).createFDApplicantRequest.NomineeDetails =
                    (activity as BajajFdMainActivity).nomineeDetails
                (activity as BajajFdMainActivity).createFDApplicantRequest.NomineeGuardianDetails =
                    (activity as BajajFdMainActivity).nomineeGuardianDetails


                (activity as BajajFdMainActivity).createFDRequest.CreateFDApplicationRequest =
                    (activity as BajajFdMainActivity).createFDApplicantRequest
                Log.e(
                    "StepTwoResponse",
                    "-->" + Gson().toJson((activity as BajajFdMainActivity).createFDRequest)
                )
                createFDApi((activity as BajajFdMainActivity).createFDRequest)
            }
        }

        binding.btnBack.setOnClickListener {
            (activity as BajajFdMainActivity).binding.viewPager.currentItem = 0
        }

    }

    private fun createFDApi(data: CreateFDRequest) {
        ProgressUtil.showLoading(activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.createFDApi(data, token, activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.getFDResponseMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("createFDApi ", " response -->$response")
                    val createFDApplicationResponse: CreateFDApplicationResponse =
                        Gson().fromJson(
                            response.data?.toString(),
                            CreateFDApplicationResponse::class.java
                        )
                    createFDApplicationResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                (activity as BajajFdMainActivity).uniqueId = createFDApplicationResponse.Response.FDCreationDetailsResponse.UniqueId
                                (activity as BajajFdMainActivity).stepTwoApi()

                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    createFDApplicationResponse.Response.Errors[0].ErrorMessage
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

    private fun ifscCodeCheckApi(ifsc: String) {
        if (ifsc.length >= 10) {
            (activity as BajajFdMainActivity).viewModel.ifscCodeApi(ifsc, activity as BajajFdMainActivity)
            (activity as BajajFdMainActivity).viewModel.getIfscCodeCheckMutableData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.e("ifscCodeCheckApi ", " response -->$response")
                        val getIFSCCodeListResponse =
                            Gson().fromJson(
                                response.data?.toString(),
                                GetIFSCCodeListResponse::class.java
                            )
                        getIFSCCodeListResponse.Response.status_code.let { code ->
                            when (code) {
                                200 -> {
                                    if (listOfIFSC.size > 0) {
                                        listOfIFSC.clear()
                                    }
                                    listOfIFSC = getIFSCCodeListResponse.IFSCCodes
                                    if (listOfIFSC.size > 0) {
                                        val adapter = ArrayAdapter(
                                            activity as BajajFdMainActivity,
                                            R.layout.spinner_dropdown,
                                            listOfIFSC
                                        )
                                        binding.edtIFSC.setAdapter(adapter)
                                        binding.edtIFSC.setOnItemClickListener { _, _, position, _ ->
                                            if (listOfIFSC.size > 0) {
                                                binding.edtIFSC.setText(
                                                    adapter.getItem(position).toString()
                                                )
                                                binding.edtIFSC.setSelection(binding.edtIFSC.text.toString().length)
                                                getIFSCDetailsApi(binding.edtIFSC.text.toString())
                                            }
                                        }
                                    }
                                }
                                //   650 -> refreshToken()
                                else -> {
                                    showDialogValidation(
                                        activity as BajajFdMainActivity,
                                        getIFSCCodeListResponse.Response.message
                                    )
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            showDialogValidation(
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

    private fun getIFSCDetailsApi(ifscCode: String) {

        (activity as BajajFdMainActivity).viewModel.ifscCodeDetailsApi(
            ifscCode,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getIfscCodeDetailsCheckMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data.toString().isNotEmpty()) {
                        Log.e("getIFSCDetailsApi ", " response -->$response")
                        val getIFSCCodeResponse = Gson().fromJson(
                            response.data.toString(),
                            GetIFSCCodeResponse::class.java
                        )
                        binding.edtBankName.setText(getIFSCCodeResponse.IFSCCODEServiceResult[0].BnkDescr)
                        binding.edtBankName.setSelection(binding.edtBankName.text.toString().length)
                        binding.edtBankBranch.setText(getIFSCCodeResponse.IFSCCODEServiceResult[0].BankBranch)
                        binding.edtBankBranch.setSelection( binding.edtBankBranch.text.toString().length)
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

    private fun verifyAccountNo(
        bankAccount: String, Ifsc: String, name: String, phoneNumber: String
    ,loginRole : Int) {

        val bankValidationApiRequest = BankValidationApiRequest()
        bankValidationApiRequest.BankAccountNo = bankAccount
        bankValidationApiRequest.IFSC = Ifsc
        bankValidationApiRequest.Name = name
        bankValidationApiRequest.PhoneNo = phoneNumber
        bankValidationApiRequest.RoleId = loginRole
        bankValidationApiRequest.BankNo = 1
        ProgressUtil.showLoading(activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.bankValidationApi(
            bankValidationApiRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.bankValidationApiMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("verifyAccountNo ", " response -->$response")
                    val getBankValidationApiResponse =
                        Gson().fromJson(
                            response.data?.toString(),
                            BankValidationApiResponse::class.java
                        )
                    getBankValidationApiResponse.response.status_code.let { code ->
                        if (binding.tvAcVerify.visibility == View.GONE) {
                            binding.tvAcVerify.visibility = View.VISIBLE
                        }
                        when (code) {
                            200 -> {
                                if (getBankValidationApiResponse.Message == getString(R.string.accountVerified)) {
                                    val clientBankList = ClientBanklist()
                                    clientBankList.AccountNumber = bankAccount
                                    clientBankList.AccountType = "sb"
                                    clientBankList.BankName =
                                        binding.edtBankName.text.toString()
                                    clientBankList.BranchName =
                                        binding.edtBankBranch.text.toString()
                                    clientBankList.IFSCCode = Ifsc
                                    clientBankList.IsValBank = 1
                                    (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.ClientBanklist?.let {
                                        listOfBanks.addAll(
                                            it
                                        )
                                    }
                                    listOfBanks.add(clientBankList)
                                    setUpRecyclerView(
                                        listOfBanks,
                                        clientBankList.AccountNumber.toString()
                                    )
                                    showHideBankDetail()

                                } else {
                                    showDialogValidation(
                                        activity as BajajFdMainActivity,
                                        getBankValidationApiResponse.Message
                                    )
                                    showHideBankDetail()
                                }
                            }
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getBankValidationApiResponse.Message
                                )
                                showHideBankDetail()
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

    private fun validateBank(): Boolean {
        return if (binding.edtIFSC.text.toString().isEmpty()) { // EditText
            commonErrorAutoCompleteMethod(
                binding.edtIFSC,
                binding.tlIFSC,
                getString(R.string.emptyIFSCCode)
            )
        } else if (binding.edtIFSC.text.toString().length != 11) { // EditText
            commonErrorAutoCompleteMethod(
                binding.edtIFSC,
                binding.tlIFSC,
                getString(R.string.validIFSCCode)
            )
        } else if (binding.edtAccountNumber.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtAccountNumber,
                binding.tlAccountNumber,
                getString(R.string.emptyAccNo)
            )
        } else if (binding.edtBankName.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtBankName,
                binding.tlBankName,
                getString(R.string.emptyBankName)
            )
        } else if (binding.edtBankBranch.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtBankBranch,
                binding.tlBankBranchName,
                getString(R.string.emptyBranchName)
            )
        } else {
            return true
        }
    }

    private fun setUpRecyclerView(
        bankList: List<ClientBanklist>?,
        selectedAccount: String? = null
    ) {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels.toDouble()
        binding.rvClientBankList.layoutManager = LinearLayoutManager(
            activity as BajajFdMainActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        bankListAdapter = BankListAdapter(bankList, selectedAccount, width)
        binding.rvClientBankList.adapter = bankListAdapter
    }

    private fun datePicker(edtDOB: TextInputEditText, number : Int) {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            activity as BajajFdMainActivity, { _, years, monthOfYear, dayOfMonth ->
                if (monthOfYear.toString().length == 1) {
                    "0".plus(monthOfYear)
                }
                edtDOB.setText(
                    years.toString().plus("-").plus(monthOfYear + 1).plus("-")
                        .plus(dayOfMonth.toString())
                )
                edtDOB.setSelection(edtDOB.text.toString().length)
                if (number == 2){
                  if (isMinor(years.toString().plus("-").plus(monthOfYear + 1).plus("-")
                        .plus(dayOfMonth.toString()))
                  ){
                      guardianCodeVisibility(View.VISIBLE)
                  }else{
                      guardianCodeVisibility(View.GONE)
                  }
                }
            }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = cal.timeInMillis
        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, -140)
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    private fun guardianCodeVisibility(visibility: Int) {
        binding.tlGuardianName.visibility = visibility
        binding.tlGuardianName.visibility = visibility
        binding.tlGuardianName.visibility = visibility
        binding.tlGuardianName.visibility = visibility
        binding.tlGuardianName.visibility = visibility

    }

    private fun panCheckApi() {
        val panCheck = PanCheckRequest()
        panCheck.clientCode =
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE
        panCheck.subBrokerCode =
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.sub_broker_code
        panCheck.panNumber = binding.edtPANNumber.text.toString()
        panCheck.mobileNumber = ""
        (activity as BajajFdMainActivity).viewModel.panCheck(panCheck, token, activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.getPanCheckMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("panCheckApi ", " response -->$response")
                    val panCheckResponse =
                        Gson().fromJson(
                            response.data?.toString(),
                            PanCheckResponse::class.java
                        )
                    panCheckResponse.response.status_code.let { code ->
                        if (binding.tvPANVerify.visibility == View.GONE) {
                            binding.tvPANVerify.visibility = View.VISIBLE
                        }
                        when (code) {
                            200 -> {
                                binding.tvPANVerify.text = getString(R.string.verifiedText)
                                binding.tvPANVerify.setTextColor(
                                    ContextCompat.getColor(
                                        activity as BajajFdMainActivity,
                                        R.color.green
                                    )
                                )
                            }
                            //   650 -> refreshToken()
                            else -> {
                                binding.tvPANVerify.text = getString(R.string.notVerifiedText)
                                binding.tvPANVerify.setTextColor(
                                    ContextCompat.getColor(
                                        activity as BajajFdMainActivity,
                                        R.color.red
                                    )
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

    private fun titleApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.salutationCategory)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.titleApi(getCodeRequest, token, activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.getTitleMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("titleApi ", " response -->$response")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfTitle = getCodeResponse.Response.GetCodesList
                                if (listOfTitle.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfTitle
                                    )
                                    binding.spTitle.setAdapter(adapter)

                                    val titleText =
                                        (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.Client_Title
                                    val newTitleText = "$titleText."

                                    if (titleText.isNullOrEmpty()) {
                                        binding.spTitle.setText(
                                            adapter.getItem(0)?.Value,
                                            false
                                        )
                                    } else {
                                        for (title in listOfTitle) {
                                            if (title.Value == newTitleText) {
                                                binding.spTitle.setText(title.Value, false)
                                                break
                                            }
                                        }
                                    }
                                    binding.spNomineeTitle.setAdapter(adapter)
                                    binding.spNomineeTitle.setText(
                                        adapter.getItem(0)?.Value,
                                        false
                                    )
                                }
                                genderApi()
                                relationShipApi()
                                maritalStatusApi()
                                occupationApi()
                                annualIncomeApi()
                                stateListApi()
                                paymentModeApi()

                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
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

    private fun paymentModeApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.paymentModeCategory)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.paymentModeApi(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getPaymentModeMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("paymentModeApi ", " response -->$response")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                if (getCodeResponse.Response.GetCodesList.isNotEmpty()) {
                                    listOfPayMode = getCodeResponse.Response.GetCodesList
                                    setUpRecyclerViewPaymentMode(listOfPayMode)
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
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

    private fun setUpRecyclerViewPaymentMode(listOfPayMode: List<GetCodes>) {

        binding.rvPaymentMode.layoutManager =
            GridLayoutManager(activity as BajajFdMainActivity, 2)
        paymentModeAdapter = PaymentModeAdapter(
            listOfPayMode,
            listOfPayMode[0].Value
        )
        binding.rvPaymentMode.adapter = paymentModeAdapter
    }

    private fun genderApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.genderCategory)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.genderApi(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getGenderMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("genderApi ", " response -->$response")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfGender = getCodeResponse.Response.GetCodesList
                                if (listOfGender.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfGender
                                    )
                                    binding.spGender.setAdapter(adapter)
                                    binding.spGender.setText(
                                        adapter.getItem(0)?.Value,
                                        false
                                    )
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
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

    private fun relationShipApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.relationshipCategory)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.relationShipApi(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getRelationShipMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("relationShipApi ", " response -->$response")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfRelationShip = getCodeResponse.Response.GetCodesList
                                if (listOfGender.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfRelationShip
                                    )

                                    binding.spNomineeRelation.setAdapter(adapter)
                                    binding.spNomineeRelation.setText(
                                        adapter.getItem(0)?.Value,
                                        false
                                    )

                                    binding.spGuardianRelation.setAdapter(adapter)
                                    binding.spGuardianRelation.setText(
                                        adapter.getItem(0)?.Value,
                                        false
                                    )
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
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

    private fun maritalStatusApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.maritalCategory)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.maritalStatusApi(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getMaritalStatusMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("maritalStatusApi ", " response -->$response")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfMaritalStatus = getCodeResponse.Response.GetCodesList
                                if (listOfMaritalStatus.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfMaritalStatus
                                    )
                                    binding.spMarital.setAdapter(adapter)
                                    binding.spMarital.setText(
                                        adapter.getItem(0)?.Value,
                                        false
                                    )
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
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

    private fun occupationApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.occupationCategory)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.occupationApi(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getOccupationMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("occupationApi ", " response -->$response")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfOccupation = getCodeResponse.Response.GetCodesList
                                if (listOfOccupation.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfOccupation
                                    )
                                    binding.spOccupation.setAdapter(adapter)
                                    binding.spOccupation.setText(
                                        adapter.getItem(0)?.Label,
                                        false
                                    )
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
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

    private fun annualIncomeApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.annualIncome)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.annualIncomeApi(
            getCodeRequest,
            token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getAnnualIncomeMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("annualIncomeApi ", " response -->$response")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfAnnualIncome = getCodeResponse.Response.GetCodesList
                                if (listOfAnnualIncome.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfAnnualIncome
                                    )
                                    binding.spIncome.setAdapter(adapter)
                                    binding.spIncome.setText(
                                        adapter.getItem(0)?.Value,
                                        false
                                    )
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Errors[0].ErrorMessage
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

    private fun stateListApi() {
        (activity as BajajFdMainActivity).viewModel.stateApi(token, activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.getStateMasterMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("stateListApi ", " response -->$response")
                    val stateListResponse: StateListResponse =
                        Gson().fromJson(
                            response.data?.toString(),
                            StateListResponse::class.java
                        )
                    stateListResponse.response.status_code.let { code ->
                        when (code) {
                            200 -> {
                                listOfStates = stateListResponse.DataObject
                                if (listOfStates.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfStates
                                    )
                                    binding.spState.setAdapter(adapter)
                                    val newTitleText =
                                        (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_STATE
                                    if (newTitleText?.isEmpty()!!) {
                                        binding.spState.setText(
                                            adapter.getItem(0)?.State_Name,
                                            false
                                        )
                                        cityListApi(adapter.getItem(0)?.State_Id)

                                        stateObject = adapter.getItem(0) as DataObject
                                        Log.e("if", " -->" + stateObject.State_Code)
                                    } else {
                                        for (title in listOfStates) {
                                            if (title.State_Code == newTitleText) {
                                                binding.spState.setText(title.State_Name, false)
                                                cityListApi(title.State_Id)
                                                stateObject = title
                                                Log.e("else", " -->" + stateObject.State_Code)
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    stateListResponse.response.message
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

    private fun cityListApi(stateCode: Int?) {

        val cityRequest = CityRequest()
        cityRequest.StateCode = stateCode
        cityRequest.Type = ""
        cityRequest.ClientCode =
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE
        cityRequest.RoleID = (activity as BajajFdMainActivity).loginRole
        cityRequest.APIName = ""
        cityRequest.UID = 0
        cityRequest.Subbroker_Code =
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.sub_broker_code
        cityRequest.RMCode = ""
        cityRequest.HOCode = ""
        cityRequest.Source = getString(R.string.source)
        cityRequest.APP_Web = getString(R.string.app)
        (activity as BajajFdMainActivity).viewModel.cityListApi(cityRequest, token, activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.getCityListMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("cityListApi ", " response -->$response")
                    val cityListResponse: CityListResponse =
                        Gson().fromJson(response.data?.toString(), CityListResponse::class.java)
                    cityListResponse.response.status_code.let { code ->
                        when (code) {
                            200 -> {
                                listOfCities = cityListResponse.DataObject
                                if (listOfCities.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfCities
                                    )
                                    binding.spCity.setAdapter(adapter)

                                    val newTitleText =
                                        (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CITY
                                    if (newTitleText?.isEmpty()!!) {
                                        binding.spCity.setText(
                                            adapter.getItem(0)?.city_name,
                                            false
                                        )
                                    } else {
                                        for (title in listOfCities) {
                                            if (title.city_name == newTitleText) {
                                                binding.spCity.setText(title.city_name, false)
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    cityListResponse.response.message
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

    private fun apiForEligibleBankList() {
        ProgressUtil.showLoading(activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.bankListApi(token,getString(R.string.language), activity as BajajFdMainActivity)
        (activity as BajajFdMainActivity).viewModel.getFDBankListMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("apiForEligibleBankList ", " response -->$response")
                    val getFDBankListResponse: GetFDBankListResponse =
                        Gson().fromJson(
                            response.data?.toString(),
                            GetFDBankListResponse::class.java
                        )
                    getFDBankListResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                if (getFDBankListResponse.Response.BankList.isNotEmpty()) {
                                    dialogForBankList(getFDBankListResponse.Response.BankList)
                                }
                            }
                            650 -> refreshToken()
                            else -> {
                                showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getFDBankListResponse.Response.Errors[0].ErrorMessage
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

    private fun refreshToken() {

    }

    private fun dialogForBankList(bankList: List<Bank>) {
        val dialog = Dialog(activity as BajajFdMainActivity)
        dialog.setContentView(R.layout.layout_bank_list)
        dialog.setCancelable(true)
        dialog.window!!
            .setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        val rvClientList = dialog.findViewById<RecyclerView>(R.id.rvBankList)
        rvClientList.layoutManager = LinearLayoutManager(activity as BajajFdMainActivity)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        if (bankList.isNotEmpty()) {
            val bankListAdapter = RecommendedBankListAdapter(bankList)
            rvClientList.adapter = bankListAdapter
        }
        dialog.show()
    }

    // validations
    private fun validation(): Boolean {
        return if (binding.edtMobileNumber.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtMobileNumber,
                binding.tlMobileNumber,
                getString(R.string.emptyMobileNumber)
            )
        } else if (!isIndianMobileNo(binding.edtMobileNumber.text.toString())) { // EditText
            commonErrorMethod(
                binding.edtMobileNumber,
                binding.tlMobileNumber,
                getString(R.string.inValidIndianMobileNumber)
            )
        } else if (binding.edtMobileNumber.text?.length != 10) { // EditText
            commonErrorMethod(
                binding.edtMobileNumber,
                binding.tlMobileNumber,
                getString(R.string.inValidMobileNumber)
            )
        } else if (binding.edtDOB.text.toString().isEmpty()) { // EditText
            commonErrorMethod(binding.edtDOB, binding.tlDOB, getString(R.string.emptyDOB))
        } else if (binding.edtPANNumber.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtPANNumber,
                binding.tlPanNumber,
                getString(R.string.emptyPAN)
            )
        } else if (!isValidPan(binding.edtPANNumber.text.toString())) { // EditText
            commonErrorMethod(
                binding.edtPANNumber,
                binding.tlPanNumber,
                getString(R.string.invalidPAN)
            )
        } else if (binding.spTitle.text.isEmpty()) { // Spinner
            commonSpinnerErrorMethod(
                binding.spTitle,
                binding.tlTitle,
                getString(R.string.emptyTitle)
            )
        } else if (binding.spGender.text.toString().isEmpty()) { // Spinner
            commonSpinnerErrorMethod(
                binding.spGender,
                binding.tlGender,
                getString(R.string.emptyGender)
            )
        } else if (binding.edtFirstName.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtFirstName,
                binding.tlFirstName,
                getString(R.string.emptyFirstName)
            )
        } else if (!isValidName(binding.edtFirstName.text.toString())) { // EditText
            commonErrorMethod(
                binding.edtFirstName,
                binding.tlFirstName,
                getString(R.string.validFirstName)
            )
        } else if (binding.edtMiddleName.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtMiddleName,
                binding.tlMiddleName,
                getString(R.string.emptyMiddleName)
            )
        } else if (!isValidName(binding.edtMiddleName.text.toString())) { // EditText
            commonErrorMethod(
                binding.edtMiddleName,
                binding.tlMiddleName,
                getString(R.string.validMiddleName)
            )
        } else if (binding.edtLastName.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtLastName,
                binding.tlLastName,
                getString(R.string.emptyLastName)
            )
        } else if (!isValidName(binding.edtLastName.text.toString())) { // EditText
            commonErrorMethod(
                binding.edtLastName,
                binding.tlLastName,
                getString(R.string.validLastName)
            )
        } else if (binding.spOccupation.text.toString().isEmpty()) { // EditText
            commonSpinnerErrorMethod(
                binding.spOccupation,
                binding.tlOccupation,
                getString(R.string.emptyOccupation)
            )
        } else if (binding.spIncome.text.toString().isEmpty()) { // EditText
            commonSpinnerErrorMethod(
                binding.spIncome,
                binding.tlIncome,
                getString(R.string.emptyIncome)
            )
        } else if (binding.edtQualification.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtQualification,
                binding.tlQualification,
                getString(R.string.emptyQualification)
            )
        } else if (binding.spMarital.text.toString().isEmpty()) {  // Spinner
            commonSpinnerErrorMethod(
                binding.spMarital,
                binding.tlMarital,
                getString(R.string.emptyMaritalStatus)
            )
        } else if (binding.edtEmail.text.toString().isEmpty()) { // EditText
            commonErrorMethod(binding.edtEmail, binding.tlEmail, getString(R.string.emptyEmail))
        } else if (!isValidEmail(binding.edtEmail.text.toString())) { // EditText
            commonErrorMethod(binding.edtEmail, binding.tlEmail, getString(R.string.validEmail))
        } else if (binding.edtAddressLine1.text.toString().isEmpty()) {  // EditText
            commonErrorMethod(
                binding.edtAddressLine1,
                binding.tlAddressLine1,
                getString(R.string.emptyAddressLine1)
            )
        } else if (binding.edtAddressLine2.text.toString().isEmpty()) {  // EditText
            commonErrorMethod(
                binding.edtAddressLine2,
                binding.tlAddressLine2,
                getString(R.string.emptyAddressLine2)
            )
        } else if (binding.edtAddressLine3.text.toString().isEmpty()) {  // EditText
            commonErrorMethod(
                binding.edtAddressLine3,
                binding.tlAddressLine3,
                getString(R.string.emptyAddressLine3)
            )
        } else if (binding.spState.text.toString().isEmpty()) {   // EditText
            commonSpinnerErrorMethod(
                binding.spState,
                binding.tlState,
                getString(R.string.emptyState)
            )
        } else if (binding.spCity.text.toString().isEmpty()) { // EditText
            commonSpinnerErrorMethod(
                binding.spCity,
                binding.tlCity,
                getString(R.string.emptyCity)
            )
        } else if (binding.edtPinCode.text.toString().isEmpty()) { // EditText
            commonErrorMethod(
                binding.edtPinCode,
                binding.tlPinCode,
                getString(R.string.emptyPinCode)
            )
        } else if (binding.edtPinCode.text.toString().length != 6) { // EditText
            commonErrorMethod(
                binding.edtPinCode,
                binding.tlPinCode,
                getString(R.string.validPinCode)
            )
        } else if (binding.edtNomineeDOB.text.toString()
                .isNotEmpty() && isMinor(binding.edtNomineeDOB.text.toString())
        ) {
            commonErrorMethod(
                binding.edtGuardianName,
                binding.tlGuardianName,
                getString(R.string.validGuardianDetails)
            )
        } else if ((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.ClientBanklist?.isEmpty()!! && binding.edtIFSC.text.toString()
                .isEmpty()
        ) { // EditText
            commonErrorAutoCompleteMethod(
                binding.edtIFSC,
                binding.tlIFSC,
                getString(R.string.emptyIFSCCode)
            )
        } else if ((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.ClientBanklist?.isEmpty()!! && binding.edtIFSC.text.toString()
                .length != 11
        ) { // EditText
            commonErrorAutoCompleteMethod(
                binding.edtIFSC,
                binding.tlIFSC,
                getString(R.string.validIFSCCode)
            )
        } else if ((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.ClientBanklist?.isEmpty()!! && binding.edtAccountNumber.text.toString()
                .isEmpty()
        ) { // EditText
            commonErrorMethod(
                binding.edtAccountNumber,
                binding.tlAccountNumber,
                getString(R.string.emptyAccNo)
            )
        } else if ((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.ClientBanklist?.isEmpty()!! && binding.edtBankName.text.toString()
                .isEmpty()
        ) { // EditText
            commonErrorMethod(
                binding.edtBankName,
                binding.tlBankName,
                getString(R.string.emptyBankName)
            )
        } else if ((activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.ClientBanklist?.isEmpty()!! && binding.edtBankBranch.text.toString()
                .isEmpty()
        ) { // EditText
            commonErrorMethod(
                binding.edtBankBranch,
                binding.tlBankBranchName,
                getString(R.string.emptyBranchName)
            )
        } else {
            return true
        }
    }

    private fun showHideBankDetail() {
        if (binding.llBankDetails.visibility == View.GONE) {
            binding.llBankDetails.visibility = View.VISIBLE
            binding.addBankDetail.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_minus,
                0
            )

        } else {
            binding.llBankDetails.visibility = View.GONE
            binding.addBankDetail.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_add_icon,
                0
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


