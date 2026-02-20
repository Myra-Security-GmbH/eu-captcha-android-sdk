package eu.eucaptcha.android.example

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class LoginResponse(val success: Boolean, val message: String, val statusCode: Int)

// 10.0.2.2 is the alias to localhost in the Android Emulator.
const val LOGIN_ENDPOINT_URL = "http://10.0.2.2:3600/login"

suspend fun doLoginRequest(
    username: String,
    password: String,
    captchaResponse: String,
): LoginResponse {
    val url = URL(LOGIN_ENDPOINT_URL)
    val json = JSONObject().apply {
        put("username", username)
        put("password", password)
        put("eu-captcha-response", captchaResponse)
    }
    val postData = json.toString().toByteArray()

    return withContext(Dispatchers.IO) {
        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.doOutput = true
            connection.outputStream.use { it.write(postData) }

            val statusCode = connection.responseCode
            return@withContext try {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseText)
                LoginResponse(
                    success = responseJson.getBoolean("success"),
                    message = responseJson.getString("message"),
                    statusCode = statusCode,
                )
            } catch (e: JSONException) {
                Log.e("example-app", "JSON parse error", e)
                LoginResponse(success = false, message = "Invalid response format", statusCode = statusCode)
            }
        } catch (e: IOException) {
            LoginResponse(
                success = false,
                message = "Request failed. Is the server running?",
                statusCode = -1,
            )
        }
    }
}
