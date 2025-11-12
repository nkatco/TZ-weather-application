package nikita.lusenkov.data.remote.core

import okhttp3.Interceptor
import okhttp3.Response

internal class ApiKeyInterceptor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("key", apiKey)
            .build()
        val newReq = original.newBuilder()
            .url(url)
            .header("Accept", "application/json")
            .build()
        return chain.proceed(newReq)
    }
}