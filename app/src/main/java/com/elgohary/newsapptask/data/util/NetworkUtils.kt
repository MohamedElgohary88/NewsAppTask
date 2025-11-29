package com.elgohary.newsapptask.data.util

import retrofit2.HttpException
import java.io.IOException

/**
 * Utility to execute a suspending network call and wrap the result into a [Result].
 *
 * Usage:
 * val result = executeSafely { apiService.getSomething() }
 * if (result.isSuccess) { ... } else { val error = result.exceptionOrNull() }
 */
suspend fun <T> executeSafely(call: suspend () -> T): Result<T> {
    return try {
        Result.success(call())
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

