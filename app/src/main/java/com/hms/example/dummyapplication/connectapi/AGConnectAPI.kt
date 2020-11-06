package com.hms.example.dummyapplication.connectapi

import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


data class AGCredential(val clientId: String, val key: String)

class AGConnectAPI(credential: AGCredential) {

    companion object {
        @JvmField
        val HOST = "https://connect-api.cloud.huawei.com/api"
        private val MISSING_CREDENTIALS = "must setup the client credentials first"
        val MISSING_CREDENTIALS_RESPONSE=ResponseObject(403, MISSING_CREDENTIALS, null)
    }


    var token: String? = null
    var credential: AGCredential= credential
    set(value) {
        field = value
        getToken()
    }

    init {
        getToken()
    }

    private fun getToken(): Int {
        val host = "$HOST/oauth2/v1/token"

        val headers = HashMap<String, String>().apply {
            put("Content-Type", "application/json")
        }

        val body = JSONObject().apply {
            put("client_id", credential.clientId)
            put("client_secret", credential.key)
            put("grant_type", "client_credentials")
        }

        val response = RequestHelper.sendRequest(host, headers, body, "POST")
        val token = response.responseBody?.let {
            if (it.has("access_token")) {
                it.getString("access_token")
            } else null

        }

        return if (token != null) {
            this.token = token
            200
        } else response.code
    }

    fun queryAppId(packageName: String): ResponseObject {
        return if (!token.isNullOrEmpty()) {
            val url = "$HOST/publish/v2/appid-list?packageName=$packageName"
            RequestHelper.sendRequest(url, getClientHeaders(), null)
        } else MISSING_CREDENTIALS_RESPONSE

    }

    fun getAppInfo(appId: String, lang: String): ResponseObject {
        return if (!token.isNullOrEmpty()) {
            val url = "$HOST/publish/v2/app-info?appid=$appId&lang=$lang"

            return RequestHelper.sendRequest(url, getClientHeaders(), null)

        } else MISSING_CREDENTIALS_RESPONSE
    }

    fun getReportUrl(
        appId: String,
        lang: String,
        startTime: String,
        endTime: String,
        filterConditions:HashMap<String,String>
    ):ResponseObject {
        return if (!token.isNullOrEmpty()){
            val fc = StringBuilder().apply {
                for (key in filterConditions.keys) {
                    append("&filterCondition=")
                    append(key)
                    append("&filterConditionValue=")
                    append(filterConditions[key])
                }
            }
            val url =
                "$HOST/report/distribution-operation-quality/v1/orderDetailExport/$appId?language=$lang&startTime=$startTime&endTime=$endTime${fc}"
            RequestHelper.sendRequest(url,getClientHeaders(),null)
        } else MISSING_CREDENTIALS_RESPONSE
    }

    fun getUploadURL(appId: String,suffix: String):ResponseObject{
        return if (!token.isNullOrEmpty()){
        val url="$HOST/publish/v2/upload-url?appId=$appId&suffix=$suffix"
        RequestHelper.sendRequest(url,getClientHeaders(),null)
        } else MISSING_CREDENTIALS_RESPONSE
    }

    private fun getClientHeaders(): HashMap<String, String> {
        return HashMap<String, String>().apply {
            put("Authorization", "Bearer $token")
            put("client_id", credential.clientId)
        }
    }


}