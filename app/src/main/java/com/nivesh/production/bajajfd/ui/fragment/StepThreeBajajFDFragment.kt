package com.nivesh.production.bajajfd.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.nivesh.production.bajajfd.BuildConfig
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.databinding.FragmentBajajfdStepThreeBinding
import com.nivesh.production.bajajfd.model.*
import com.nivesh.production.bajajfd.ui.activity.BajajFdMainActivity
import com.nivesh.production.bajajfd.util.Common
import com.nivesh.production.bajajfd.util.Common.Companion.getFileExtension
import com.nivesh.production.bajajfd.util.Common.Companion.showDialogWithTwoButtons
import com.nivesh.production.bajajfd.util.Constants
import com.nivesh.production.bajajfd.util.ImageUtil
import com.nivesh.production.bajajfd.util.Resource
import java.io.*
import java.util.*

class StepThreeBajajFDFragment : Fragment() {

    private var _binding: FragmentBajajfdStepThreeBinding? = null
    private val binding get() = _binding!!

    private var takeImageResult: ActivityResultLauncher<Uri>? = null
    private var selectImageIntent: ActivityResultLauncher<String>? = null
    private var bitmap: Bitmap? = null
    private var latestTmpUri: Uri? = null

    private val mapImage: HashMap<String, String> = HashMap()
    private lateinit var listOfDocType: List<GetCodes>

    private var panString: String = ""
    private var photoString: String = ""
    private var docString: String = ""
    private var docString2: String = ""
    private var panFileExt: String? = ""
    private var photoFileExt: String? = ""
    private var doc1FileExt: String? = ""
    private var doc2fileExt: String? = ""
    private var docValue: String = ""
    private val mainPANUpload: Int = 1
    private val mainPhotoUpload: Int = 2
    private val firstDocUpload: Int = 3
    private val secondDocUpload: Int = 4
    private var actionType: Int = -1

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            takeImage()
        } else {
            showDialogWithTwoButtons(
                (activity as BajajFdMainActivity), getString(R.string.cameraPermission), getString(
                    R.string.permissionRequired
                )
            )
        }
    }

    private val requestGalleryPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        if (!permission.containsValue(false)) {
            selectImageIntent?.launch("image/*")
        } else {
            showDialogWithTwoButtons(
                (activity as BajajFdMainActivity), getString(R.string.galleryPermission), getString(
                    R.string.permissionsRequired
                )
            )
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBajajfdStepThreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent())
        { uri: Uri? ->
            if (uri != null) {
                bitmap = uriToBitmap(uri)
                uploadDocument(uri, "g")
            }
        }

        takeImageResult =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                if (isSuccess) {
                    latestTmpUri?.let { uri ->
                        uploadDocument(uri, "c")
                    }
                }
            }

        binding.btnNext.setOnClickListener {
            if (validate()) {
                if (panString.isNotEmpty()) {
                    mapImage["PAN"] = "data:image/".plus(panFileExt)
                        .plus(";base64, ").plus(panString)
                }
                if (photoString.isNotEmpty()) {
                    mapImage["Photograph"] = "data:image/".plus(photoFileExt)
                        .plus(";base64, ").plus(photoString)
                }
                if (docString.isNotEmpty()) {
                    mapImage["docValue"] = "data:image/".plus(doc1FileExt)
                        .plus(";base64, ").plus(docString)
                }
                if (docString2.isNotEmpty()) {
                    mapImage["Aadhar"] = "data:image/".plus(doc2fileExt)
                        .plus(";base64, ").plus(docString2)
                }
                var uploadPosition = 0
                for (entry in mapImage.iterator()) {
                    uploadPosition++
                    uploadDocApi(entry.key, entry.value, uploadPosition)
                }
            }
        }
        binding.btnBack.setOnClickListener {
            (activity as BajajFdMainActivity).binding.viewPager.currentItem = 1
        }

        binding.btnPANUpload.setOnClickListener {
            actionType = mainPANUpload
            selectImage()
        }

        binding.btnPhotoUpload.setOnClickListener {
            actionType = mainPhotoUpload
            selectImage()
        }

        binding.btnAadhaarFrontUpload.setOnClickListener {
            actionType = firstDocUpload
            selectImage()
        }

        binding.btnAadhaarBackUpload.setOnClickListener {
            actionType = secondDocUpload
            selectImage()
        }

        binding.spDocType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val getCodes: GetCodes = parent.getItemAtPosition(position) as GetCodes
                docValue = getCodes.Value
                docString = ""
                docString2 = ""
                when (getCodes.Label) {
                    resources.getString(R.string.aadhar) ->
                        setAadhaarUploadLayout()
                    else -> {
                        setOtherUploadLayout(getCodes.Value)
                    }
                }
            }

        docTypeApi()
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String {
        var result = ""
        if (uri.scheme == "content") {
            val cursor: Cursor =
                requireActivity().contentResolver.query(uri, null, null, null, null)!!
            cursor.use { cursor1 ->
                if (cursor1.moveToFirst()) {
                    result = cursor1.getString(cursor1.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result.isEmpty()) {
            result = uri.path!!
            val cut = result.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun selectImage() {
        val builder = AlertDialog.Builder(
            activity as BajajFdMainActivity
        )
        builder.setTitle(getString(R.string.addPhoto))
        builder.setItems(
            arrayOf(
                getString(R.string.takePhoto),
                getString(R.string.chooseFromGallery),
                getString(R.string.cancel)
            )
        ) { dialog: DialogInterface, pos ->
            when (pos) {
                0 -> {
                    if (hasPermissions(
                            activity as BajajFdMainActivity,
                            Manifest.permission.CAMERA
                        )
                    ) {
                        takeImage()
                    } else {
                        onClickRequestPermission()
                    }
                    dialog.dismiss()
                }
                1 -> {
                    if (hasPermissions(activity as BajajFdMainActivity, *permissions)) {
                        selectImageIntent?.launch("image/*")
                    } else {
                        requestGalleryPermission.launch(permissions)
                    }
                    dialog.dismiss()
                }
                else -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun hasPermissions(activity: Activity, vararg permissions: String?): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                activity as BajajFdMainActivity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {

            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity as BajajFdMainActivity,
                Manifest.permission.CAMERA
            ) -> {
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            else -> {
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun setOtherUploadLayout(itemName: String?) {
        binding.tvAadhaarFront.text = itemName.plus(" Front *")
        if (binding.tvAadhaarBack.visibility == View.VISIBLE) binding.tvAadhaarBack.visibility =
            View.INVISIBLE
        if (binding.btnAadhaarBackUpload.visibility == View.VISIBLE) binding.btnAadhaarBackUpload.visibility =
            View.INVISIBLE
    }

    private fun setAadhaarUploadLayout() {
        binding.tvAadhaarFront.text = resources.getString(R.string.aadhaarFront)
        binding.tvAadhaarBack.text = resources.getString(R.string.aadhaarBack)

        binding.tvAadhaarBack.visibility = View.VISIBLE
        binding.btnAadhaarBackUpload.visibility = View.VISIBLE

        binding.tvAadhaarFront.visibility = View.VISIBLE
        binding.btnAadhaarFrontUpload.visibility = View.VISIBLE
    }

    private fun uploadDocument(uri: Uri, type: String) {
        when (actionType) {
            mainPANUpload -> {
                binding.ivPan.visibility = View.VISIBLE
                val fileDir: File = requireActivity().cacheDir
                val fileExtension = File(fileDir.toString().plus("/").plus(getFileName(uri)))
                panFileExt = getFileExtension(getFileName(uri))
                val size: Double = Common.getFileSizeInMB(fileExtension.length())
                if (size < 5) {
                    if (type == "c") encodedPANBase64(fileExtension)
                    else panString = bitmap?.let { ImageUtil.convert(it) }.toString()
                } else {
                    panString = ""
                    panFileExt = ""
                }
            }
            mainPhotoUpload -> {
                binding.ivPhotograph.visibility = View.VISIBLE
                binding.ivPan.visibility = View.VISIBLE
                val fileDir: File = requireActivity().cacheDir
                val fileExtension = File(fileDir.toString().plus("/").plus(getFileName(uri)))
                photoFileExt = getFileExtension(getFileName(uri))
                val size: Double = Common.getFileSizeInMB(fileExtension.length())
                if (size < 5) {
                    if (type == "c") encodedPhotoBase64(fileExtension)
                    else photoString = bitmap?.let { ImageUtil.convert(it) }.toString()
                } else {
                    photoString = ""
                    photoFileExt = ""
                }
            }
            firstDocUpload -> {
                binding.ivAadharFront.visibility = View.VISIBLE
                val fileDir: File = requireActivity().cacheDir
                val fileExtension = File(fileDir.toString().plus("/").plus(getFileName(uri)))
                doc1FileExt = getFileExtension(getFileName(uri))
                val size: Double = Common.getFileSizeInMB(fileExtension.length())
                if (size < 5) {
                    if (type == "c") encodedUpload1Base64(fileExtension)
                    else docString = bitmap?.let { ImageUtil.convert(it) }.toString()
                } else {
                    docString = ""
                    doc1FileExt = ""
                }
            }
            else -> {
                binding.ivAadharBack.visibility = View.VISIBLE
                val fileDir: File = requireActivity().cacheDir
                val fileExtension = File(fileDir.toString().plus("/").plus(getFileName(uri)))
                doc2fileExt = getFileExtension(getFileName(uri))
                val size: Double = Common.getFileSizeInMB(fileExtension.length())
                if (size < 5) {
                    if (type == "c") encodedFileToBase64(fileExtension)
                    else docString2 = bitmap?.let { ImageUtil.convert(it) }.toString()
                } else {
                    docString2 = ""
                    doc2fileExt = ""
                }
            }
        }
    }

    private fun encodedPANBase64(fileName: File) {
        panString = try {
            val bytes: ByteArray = loadFile(fileName)
            Base64.encodeToString(bytes, Base64.DEFAULT).trim().replace("\n", "")
                .replace("\\s+", "")
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun encodedPhotoBase64(fileName: File) {
        photoString = try {
            val bytes: ByteArray = loadFile(fileName)
            Base64.encodeToString(bytes, Base64.DEFAULT).trim().replace("\n", "")
                .replace("\\s+", "")
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun encodedUpload1Base64(fileName: File) {
        docString = try {
            val bytes: ByteArray = loadFile(fileName)
            Base64.encodeToString(bytes, Base64.DEFAULT).trim().replace("\n", "")
                .replace("\\s+", "")
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun encodedFileToBase64(fileName: File) {
        docString2 = try {
            val bytes: ByteArray = loadFile(fileName)
            Base64.encodeToString(bytes, Base64.DEFAULT).trim().replace("\n", "")
                .replace("\\s+", "")
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @Throws(IOException::class)
    private fun loadFile(file: File): ByteArray {
        val inputStream: InputStream = FileInputStream(file)
        val length = file.length()
        val bytes = ByteArray(length.toInt())
        var offset = 0
        var numRead = 0
        while (offset < bytes.size && inputStream.read(bytes, offset, bytes.size - offset).also {
                numRead = it
            } >= 0) {
            offset += numRead
        }
        if (offset < bytes.size) {
            throw IOException("Could not completely read file " + file.name)
        }
        inputStream.close()
        return bytes
    }

    private fun uploadDocApi(key: String, imageBase64: String, uploadPosition: Int) {
        val du = DocumentUpload()
        du.Description = key
        du.DocumentType = key
        du.FDProvider = getString(R.string.bajaj)
        du.ImageEncodeToBase64 = imageBase64
        du.NiveshClientCode =
            (activity as BajajFdMainActivity).getClientDetailsResponse.ObjectResponse?.clientDetails?.clientMasterMFD?.CLIENT_CODE
        du.UniqueId = (activity as BajajFdMainActivity).uniqueId
        (activity as BajajFdMainActivity).viewModel.documentsUpload(
            du,
            Constants.token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getDocumentUploadMutableData.observe(
            viewLifecycleOwner
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("UploadImage", "Response-->" + response.data.toString())
                    val getUploadResponse: UploadResponse =
                        Gson().fromJson(response.data?.toString(), UploadResponse::class.java)
                    getUploadResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                Log.e("check_upload_res", response.message.toString())
                                if (uploadPosition == mapImage.size) {
                                    Toast.makeText(
                                        requireActivity(),
                                        "Documents Uploaded Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    createFDApi((activity as BajajFdMainActivity).createFDRequest)
                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                Common.showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getUploadResponse.Response.Errors[0].ErrorMessage
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

    private fun createFDApi(data: CreateFDRequest) {
        (activity as BajajFdMainActivity).viewModel.createFDApi(
            data,
            Constants.token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getFDResponseMutableData.observe(
            viewLifecycleOwner
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("createFDApi", "response--> " + response.data.toString())
                    val createFDApplicationResponse: CreateFDApplicationResponse =
                        Gson().fromJson(
                            response.data?.toString(),
                            CreateFDApplicationResponse::class.java
                        )
                    createFDApplicationResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                (activity as BajajFdMainActivity).stepThreeApi()
                            }
                            //  650 -> refreshToken()
                            else -> {
                                Common.showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    createFDApplicationResponse.Response.Errors[0].ErrorMessage
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

    private fun docTypeApi() {
        val getCodeRequest = GetCodeRequest()
        getCodeRequest.ProductName = getString(R.string.bajajFD)
        getCodeRequest.Category = getString(R.string.docType)
        getCodeRequest.Language = getString(R.string.language)
        getCodeRequest.InputValue = ""
        (activity as BajajFdMainActivity).viewModel.docTypeApi(
            getCodeRequest,
            Constants.token,
            activity as BajajFdMainActivity
        )
        (activity as BajajFdMainActivity).viewModel.getDocTypeMutableData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.e("response", "-->$response")
                    val getCodeResponse: GetCodeResponse =
                        Gson().fromJson(response.data?.toString(), GetCodeResponse::class.java)
                    getCodeResponse.Response.StatusCode.let { code ->
                        when (code) {
                            200 -> {
                                listOfDocType = getCodeResponse.Response.GetCodesList
                                if (listOfDocType.isNotEmpty()) {
                                    val adapter = ArrayAdapter(
                                        activity as BajajFdMainActivity,
                                        R.layout.spinner_dropdown,
                                        listOfDocType
                                    )
                                    binding.spDocType.setAdapter(adapter)
                                    binding.spDocType.setText(
                                        adapter.getItem(0)?.Label,
                                        false
                                    )
                                    setOtherUploadLayout(adapter.getItem(0)?.Label)
                                    docValue = adapter.getItem(0)?.Label.toString()

                                }
                            }
                            //  650 -> refreshToken()
                            else -> {
                                Common.showDialogValidation(
                                    activity as BajajFdMainActivity,
                                    getCodeResponse.Response.Message
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

    private fun validate(): Boolean {
        if (panString.isEmpty()) {
            Common.showDialogValidation(
                activity as BajajFdMainActivity,
                getString(R.string.uploadPanDoc)
            )
            return false
        } else if (photoString.isEmpty()) {
            Common.showDialogValidation(
                activity as BajajFdMainActivity,
                getString(R.string.uploadPhotoDoc)
            )
            return false
        } else if (docString.isEmpty()) {
            Common.showDialogValidation(
                activity as BajajFdMainActivity,
                "Upload $docValue Document"
            )
            return false
        } else if (docValue == "Aadhar" && docString2.isEmpty()) {
            Common.showDialogValidation(
                activity as BajajFdMainActivity,
                getString(R.string.uploadAadharBackDoc)
            )
            return false
        }
        return true
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult?.launch(uri)
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile =
            File.createTempFile("tmp_image_file", ".png", requireActivity().cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }

        return FileProvider.getUriForFile(
            requireActivity(),
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor =
                requireActivity().contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}