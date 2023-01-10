package com.nivesh.production.bajajfd.api

import com.nivesh.production.bajajfd.util.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class ApiCallback<T : Any> : Callback<Resource<T>> {

    abstract fun onSuccess(response: Resource<T>)

    abstract fun onFailure(response: Resource<T>)

    override fun onResponse(call: Call<Resource<T>>, response: Response<Resource<T>>) {
        if (response.isSuccessful && response.body() != null && response.code() == 200) {
            onSuccess(response.body()!!)
        } else {
            // handle 4xx & 5xx error codes here
//            val resp = Resource<T>()
//            resp.status = false
//            resp.message = response.message()
//            onFailure(resp)



        }
    }

    override fun onFailure(call: Call<Resource<T>>, t: Throwable) {
//        val response = Resource<T>()
//        response.status = false
//        response.message = t.message.toString()
//        onFailure(response)
    }
}