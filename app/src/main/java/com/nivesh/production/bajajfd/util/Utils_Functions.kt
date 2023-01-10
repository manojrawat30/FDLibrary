package com.nivesh.production.bajajfd.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Utils_Functions {

    companion object{

        fun getCurrentDateStamp(): String? {
            val d = Date()
            @SuppressLint("SimpleDateFormat") val simpleDateFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateStr = simpleDateFormat.format(d)
            val output = d.time / 1000L
            val str = java.lang.Long.toString(output)
            return d.time.toString()
        }
    }
}