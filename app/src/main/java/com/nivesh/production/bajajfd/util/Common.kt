package com.nivesh.production.bajajfd.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.ui.activity.BajajFdMainActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Common {

    companion object {
        /**
         *Before use this method write following code in model class
        app:Application(in activity and model)
        changes in hasInternetConnection
        val connectivityManager = getApplication<NewsApplication>().getSystemService(....
         **/
        //internet check
        fun isNetworkAvailable(activity: Activity): Boolean {
            val connectivityManager = activity.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val capabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.activeNetworkInfo?.run {
                    return when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
            return false
        }

        //valid email check
        fun isValidEmail(target: String?): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(target.toString()).matches()
        }

        //valid Name Check
        fun isValidName(nameText: String?): Boolean {
            val pattern = Pattern.compile(("^[a-zA-Z\\s]{2,70}$"))
            val matcher = pattern.matcher(nameText.toString())
            return matcher.matches()
        }

        //validPanCard
        fun isValidPan(pan: String?): Boolean {
            val mPattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]")
            val mMatcher = mPattern.matcher(pan.toString())
            return mMatcher.matches()
        }

        //is Indian mobile Number
        fun isIndianMobileNo(mobileNumber: String?): Boolean {
            //(0/91): number starts with (0/91)
            //[7-9]: starting of the number may contain a digit between 0 to 9
            //[0-9]: then contains digits 0 to 9
            val pattern: Pattern = Pattern.compile("^[6-9]\\d{9}$")
            //the matcher() method creates a matcher that will match the given input against this pattern
            val match: Matcher = pattern.matcher(mobileNumber.toString())
            //returns a boolean value
            return match.matches()
        }

        fun removeError(textInputLayout: TextInputLayout) {
            if (textInputLayout.error != null) {
                textInputLayout.error = null
                if (textInputLayout.isErrorEnabled) {
                    textInputLayout.isErrorEnabled = false
                }
            }
        }

        fun showDialogValidation(activity: Activity?, message: CharSequence?) {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(message)
            builder.setCancelable(false)
            if (activity != null) {
                builder.setPositiveButton(activity.getString(R.string.Ok)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            }
            builder.show()
        }

        fun showDialogWithTwoButtons(activity: Activity?, message: CharSequence?, title: String) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
            builder.setMessage(message)
            if (activity != null) {
                builder.setPositiveButton(activity.getString(R.string.Ok)) { dialogInterface, _ ->
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", (activity as BajajFdMainActivity).packageName, null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity.startActivity(intent)
                    dialogInterface.dismiss()
                }
                builder.setNegativeButton(activity.getString(R.string.cancel)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            }
            builder.show()
        }

        // set Default Step
        fun defaultShape(): GradientDrawable {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setColor(Color.WHITE)
            shape.setStroke(6, Color.parseColor(Colors.colorDefault))
            return shape
        }

        // set Selected Step
        fun selectedShape(): GradientDrawable {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setColor(Color.parseColor(Colors.colorDefault))
            shape.setStroke(6, Color.parseColor(Colors.colorDefault))
            return shape
        }

        fun commonErrorMethod(
            inputText: TextInputEditText,
            inputError: TextInputLayout,
            strMessage: String
        ): Boolean {
            inputText.requestFocus()
            inputError.error = strMessage
            return false
        }

        fun commonErrorAutoCompleteMethod(
            inputText: MaterialAutoCompleteTextView,
            inputError: TextInputLayout,
            strMessage: String
        ): Boolean {
            inputText.requestFocus()
            inputError.error = strMessage
            return false
        }

        fun commonSpinnerErrorMethod(
            inputText: MaterialAutoCompleteTextView,
            inputError: TextInputLayout,
            strMessage: String
        ): Boolean {
            inputText.requestFocus()
            inputError.error = strMessage
            return false
        }

        fun getDate(string: String): String {
            val input = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val mDate: Date? = input.parse(string)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return simpleDateFormat.format(mDate as Date)
        }

        fun handleResponse(response: Response<JsonObject>): Resource<JsonObject> {
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let { resultResponse ->
                    return Resource.Success(resultResponse)
                }
            }
            return Resource.Error(response.message())
        }

         fun handleResponse1(response: Response<String>): Resource<String> {
             if (response.isSuccessful && response.body() != null) {
                 return if (response.body().toString().isNotEmpty()) {
                     Log.e("response", "-->$response")
                     val str: String = response.body().toString().replace("\r\n", "")
                     Log.e("str", "-->$str")
                     val jsonObject = JSONObject(str)
                     Log.e("jsonObject", "-->$jsonObject")
                     Resource.Success(jsonObject.toString())
                 }else {
                     Resource.Error(response.message())
                 }
             }
             return Resource.Error(response.message())

        }

        fun handleError(activity: Activity): CoroutineExceptionHandler {
            val handler = CoroutineExceptionHandler { _, exception ->
                if (exception is IOException || exception is HttpException) {
                    showDialogValidation(
                        activity,
                        "Response : ".plus(exception.message).plus(" Cause: ").plus(exception.cause)
                    )
                } else {
                    showDialogValidation(
                        activity,
                        "Response : ".plus(exception.localizedMessage?.toString())
                    )
                }
            }
            return handler
        }

        fun isMinor(date: String): Boolean {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dt1: Date = simpleDateFormat.parse(date) as Date
            val year: Int = DateFormat.format("yyyy", dt1).toString().toInt()
            val month: Int = DateFormat.format("mm", dt1).toString().toInt()
            val day = DateFormat.format("dd", dt1).toString().toInt()
            val userAge: Calendar = GregorianCalendar(year, month, day)
            val minAdultAge: Calendar = GregorianCalendar()
            minAdultAge.add(Calendar.YEAR, -18)
            return minAdultAge.before(userAge)
        }

        /* this function is used for file size in readable format(End)*/
        fun getFileSizeInMB(length: Long): Double {
            // Get length of file in bytes
            val fileSizeInBytes = length.toDouble()
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            val fileSizeInKB = fileSizeInBytes / 1024
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            return fileSizeInKB / 1024
        }

        fun getFileExtension(name: String): String {
            val index = name.lastIndexOf('.')
            return if (index > 0) {
                name.substring(index + 1)
            } else {
                ""
            }
        }



    }
}