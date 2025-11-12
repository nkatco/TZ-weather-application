package nikita.lusenkov.data.remote.core

import java.io.EOFException
import java.io.IOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

/**
 * чисто методы расширения верхнего уровня
 */

sealed class NetworkError(message: String? = null, cause: Throwable? = null) : Throwable(message, cause) {
    class Http(
        val code: Int,
        val body: String?,
        message: String? = null,
        cause: Throwable? = null
    ) : NetworkError(message, cause)

    class Network(io: IOException) : NetworkError(io.message, io)
    class Serialization(cause: Throwable) : NetworkError(cause.message, cause)
    class Unknown(cause: Throwable) : NetworkError(cause.message, cause)
}

internal suspend inline fun <T> safeApiCall(crossinline block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (t: Throwable) {
        Result.failure(mapNetworkError(t))
    }
}

private fun mapNetworkError(t: Throwable): Throwable = when (t) {
    is HttpException -> {
        val code = t.code()
        val body = try { t.response()?.errorBody()?.string() } catch (_: Throwable) { null }
        NetworkError.Http(code, body, message = t.message(), cause = t)
    }
    is IOException -> NetworkError.Network(t)
    is SerializationException, is EOFException -> NetworkError.Serialization(t)
    else -> NetworkError.Unknown(t)
}