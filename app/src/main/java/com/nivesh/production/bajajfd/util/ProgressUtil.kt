package com.nivesh.production.bajajfd.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.ProgressBar

@SuppressLint("StaticFieldLeak")
object ProgressUtil{

    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private lateinit var pDialog: ProgressBar

    fun showLoading(ctx: Context){
        // instantiating the lateInit objects
        dialogBuilder= AlertDialog.Builder(ctx)
        pDialog= ProgressBar(ctx)

        // setting up the dialog
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(pDialog)
        alertDialog=dialogBuilder.create()

        // magic of transparent background goes here
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // setting the alertDialog's BackgroundDrawable as the color resource of any color with 1% opacity
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00141414")))

        // finally displaying the Alertdialog containing the ProgressBar
        alertDialog.show()

    }


    fun hideLoading(){
        try {
            if(alertDialog.isShowing){
                alertDialog.dismiss()
            }
        } catch (e: UninitializedPropertyAccessException) {
              Log.e("TAG","AlertDialog UninitializedPropertyAccessException")
        }
    }
}