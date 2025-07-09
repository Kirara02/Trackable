package com.uniguard.trackable.presentation.state

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(
        val message: String,
        val code: Int? = null,
        val data: T? = null
    ) : Resource<T>()
    data class Loading<T>(val data: T? = null) : Resource<T>()
}

val Resource<*>.isLoading: Boolean
    get() = this is Resource.Loading

val Resource<*>.isSuccess: Boolean
    get() = this is Resource.Success<*>

val Resource<*>.isError: Boolean
    get() = this is Resource.Error<*>