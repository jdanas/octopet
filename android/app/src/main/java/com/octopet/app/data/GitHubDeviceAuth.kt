package com.octopet.app.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.FormBody
import okhttp3.Request
import org.json.JSONObject

const val GITHUB_CLIENT_ID = "Ov23liaP6lM1pGCnQl17"
private const val DEVICE_SCOPE = "repo read:user read:org"

data class DeviceCodeResponse(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresIn: Int,
    val interval: Int,
)

sealed class AuthPollResult {
    data class Success(val token: String) : AuthPollResult()
    data class Error(val message: String) : AuthPollResult()
    object Pending : AuthPollResult()
    object Expired : AuthPollResult()
}

suspend fun requestDeviceCode(): ApiResult<DeviceCodeResponse> {
    return try {
        val body = FormBody.Builder()
            .add("client_id", GITHUB_CLIENT_ID)
            .add("scope", DEVICE_SCOPE)
            .build()
        val request = Request.Builder()
            .url("https://github.com/login/device/code")
            .addHeader("Accept", "application/json")
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        val json = JSONObject(response.body?.string() ?: "{}")
        if (!response.isSuccessful) return ApiResult.Error("HTTP ${response.code}")

        Log.d(TAG, "Device code requested: userCode=${json.optString("user_code")}")

        ApiResult.Success(
            DeviceCodeResponse(
                deviceCode      = json.getString("device_code"),
                userCode        = json.getString("user_code"),
                verificationUri = json.getString("verification_uri"),
                expiresIn       = json.getInt("expires_in"),
                interval        = json.getInt("interval"),
            )
        )
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Failed to request device code")
    }
}

fun pollForToken(deviceCode: String, intervalSeconds: Int): Flow<AuthPollResult> = flow {
    while (true) {
        delay(intervalSeconds * 1000L)
        val body = FormBody.Builder()
            .add("client_id", GITHUB_CLIENT_ID)
            .add("device_code", deviceCode)
            .add("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
            .build()
        val request = Request.Builder()
            .url("https://github.com/login/oauth/access_token")
            .addHeader("Accept", "application/json")
            .post(body)
            .build()
        try {
            val response = client.newCall(request).execute()
            val json = JSONObject(response.body?.string() ?: "{}")
            when {
                json.has("access_token") -> {
                    Log.d(TAG, "OAuth token received")
                    emit(AuthPollResult.Success(json.getString("access_token")))
                    return@flow
                }
                json.has("error") -> when (json.getString("error")) {
                    "authorization_pending" -> emit(AuthPollResult.Pending)
                    "slow_down"            -> { delay(5000); emit(AuthPollResult.Pending) }
                    "expired_token"        -> { emit(AuthPollResult.Expired); return@flow }
                    "access_denied"        -> { emit(AuthPollResult.Error("Access denied")); return@flow }
                    else -> { emit(AuthPollResult.Error(json.optString("error_description", "Unknown error"))); return@flow }
                }
                else -> emit(AuthPollResult.Pending)
            }
        } catch (e: Exception) {
            emit(AuthPollResult.Error(e.message ?: "Poll failed"))
            return@flow
        }
    }
}.flowOn(Dispatchers.IO)
