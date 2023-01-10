package com.nivesh.production.bajajfd.util

import android.content.Context
import androidx.preference.PreferenceManager

class SharedPrefrenceDataMethods {

    companion object {

        fun getLoginEmail(Name: String?, context: Context?): String? {
            val preferences = context?.let {
                PreferenceManager.getDefaultSharedPreferences(
                    it
                )
            }

            return preferences?.getString(Name, "")

        }

        fun getLoginUserCode(Name: String?, context: Context?): String? {
            val preferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            return preferences?.getString(Name, "")
        }

        fun setLogin_SOCIALID(json: String?, context: Context?, Name: String?) {
            val preferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            val editor = preferences?.edit()
            editor?.putString(Name, json)
            editor?.apply()
        }

        fun getLoginSOCIALID(Name: String?, context: Context?): String? {
            val preferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            return preferences?.getString(Name, "")
        }

        fun getLoginPassword(Name: String?, context: Context?): String? {
            val preferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            return preferences?.getString(Name, "")
        }

        fun getLogin_Type(Name: String?, context: Context?): String? {
            val preferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            return preferences?.getString(Name, "")
        }

        fun getVersionCode(Name: String?, context: Context?): String? {
            val preferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            return preferences?.getString(Name, "")
        }

        fun getGcmAppId(Name: String?, context: Context?): String? {
            val preferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            return preferences?.getString(Name, "")
        }

        fun setToken(context: Context?, token: String?) {
            val preferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            val editor = preferences?.edit()
            editor?.putString("token", token)
            editor?.apply()
        }
    }
}