package com.nivesh.production.bajajfd.interfaces

import com.nivesh.production.bajajfd.model.CreateFDApplicationResponse

interface SendData {
    fun sendDataFragment(message: CreateFDApplicationResponse)
    fun sendDataFragmentStepFour(message: CreateFDApplicationResponse)
}