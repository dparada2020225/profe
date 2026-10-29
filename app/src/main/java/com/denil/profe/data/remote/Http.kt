package com.denil.profe.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.net.UnknownHostException
import java.net.SocketTimeoutException

/** Error de red con un mensaje listo para mostrar en español. */
class NetworkException(message: String, val code: Int = -1, cause: Throwable? = null) : IOException(message, cause)

/** Cliente HTTP mínimo (sin dependencias) para las APIs gratuitas. */
object Http {
    private const val USER_AGENT = "ProfeApp/1.0 (Android; personal English tutor)"

    data class Response(val code: Int, val body: String)

    suspend fun get(url: String, headers: Map<String, String> = emptyMap(), timeoutMs: Int = 15_000): Response =
        request("GET", url, null, null, headers, timeoutMs)

    suspend fun postForm(url: String, form: Map<String, String>, timeoutMs: Int = 20_000): Response {
        val body = form.entries.joinToString("&") { (k, v) -> enc(k) + "=" + enc(v) }
        return request("POST", url, body, "application/x-www-form-urlencoded; charset=UTF-8", emptyMap(), timeoutMs)
    }

    suspend fun postJson(url: String, json: String, headers: Map<String, String> = emptyMap(), timeoutMs: Int = 60_000): Response =
        request("POST", url, json, "application/json; charset=UTF-8", headers, timeoutMs)

    fun enc(s: String): String = URLEncoder.encode(s, "UTF-8")

    private suspend fun request(
        method: String,
        url: String,
        body: String?,
        contentType: String?,
        headers: Map<String, String>,
        timeoutMs: Int
    ): Response = withContext(Dispatchers.IO) {
        val conn = (URL(url).openConnection() as HttpURLConnection)
        try {
            conn.requestMethod = method
            conn.connectTimeout = timeoutMs
            conn.readTimeout = timeoutMs
            conn.setRequestProperty("User-Agent", USER_AGENT)
            conn.setRequestProperty("Accept", "application/json")
            headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
            if (body != null) {
                conn.doOutput = true
                if (contentType != null) conn.setRequestProperty("Content-Type", contentType)
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            Response(code, text)
        } catch (e: UnknownHostException) {
            throw NetworkException("Sin conexión a internet.", cause = e)
        } catch (e: SocketTimeoutException) {
            throw NetworkException("El servidor tardó demasiado en responder.", cause = e)
        } catch (e: IOException) {
            if (e is NetworkException) throw e
            throw NetworkException("Error de red: ${e.message ?: "desconocido"}", cause = e)
        } finally {
            conn.disconnect()
        }
    }
}
