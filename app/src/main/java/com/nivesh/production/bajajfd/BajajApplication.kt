package com.nivesh.production.bajajfd

import android.app.Application
import android.content.Context

class BajajApplication : Application() {
    private var mInstance: BajajApplication? = null
   companion object {
        var appContext: Context? = null
   }
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        appContext = applicationContext
    }

}