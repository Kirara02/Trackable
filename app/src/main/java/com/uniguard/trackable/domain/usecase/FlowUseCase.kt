package com.uniguard.trackable.domain.usecase

import android.util.Log
import com.google.gson.Gson
import com.uniguard.trackable.data.remote.response.BaseResponse
import com.uniguard.trackable.presentation.state.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

abstract class FlowUseCase<in P, R>(
    private val gson: Gson
) {
    operator fun invoke(params: P): Flow<Resource<R>> = flow {
        emit(Resource.Loading())
        try {
            emit(execute(params))
        } catch (e: Exception) {
            emit(handleException(e))
        }
    }

    protected abstract suspend fun execute(params: P): Resource<R>

    private fun handleException(e: Exception): Resource.Error<R> {
        Log.d("handleException", "handleException: ${e.message}")
        if (e is HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val parsed = try {
                gson.fromJson(errorBody, BaseResponse::class.java)
            } catch (_: Exception) {
                null
            }

            val message = parsed?.message ?: e.message()
            val code = parsed?.error?.code ?: e.code()

            return Resource.Error(message = message, code = code)
        }

        return Resource.Error(e.message ?: "Unexpected error")
    }
}
