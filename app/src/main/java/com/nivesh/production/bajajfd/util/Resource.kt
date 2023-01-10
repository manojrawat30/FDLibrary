package com.nivesh.production.bajajfd.util

sealed class Resource<T>(
    val data: T? = null,
    var message: String? = null,
    val errorCode: Int? = null,
    var status: Boolean = true

) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
    class DataError<T>(errorCode: Int, message: String) :
        Resource<T>(null, message, errorCode)

}