package com.example.domain.common

sealed class RequestResult<out BODY> {
    object HttpException : RequestResult<Nothing>()
    object IOException : RequestResult<Nothing>()
    data class Loading<T>(val isLoading: Boolean) : RequestResult<T>()
    data class Success<T>(val body: T) : RequestResult<T>()
}
