package com.hms.example.dummyapplication.connectapi

//import org.apache.http.HttpEntity
//import org.apache.http.HttpStatus
//import org.apache.http.client.methods.HttpPost
//import org.apache.http.entity.mime.MultipartEntityBuilder
//import org.apache.http.entity.mime.content.FileBody
//import org.apache.http.impl.client.HttpClients
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.MessageFormat


class ConnectApiHelper() {
    companion object{
        @JvmField val CLIENT_ID = "464633162153067712"
        @JvmField val CLIENT_SECRET ="9CB7F2D330C96B8F483CAF7DF90093487DFB506A42B87320178500D1B7EF0A82"
    }

    private val api:AGConnectAPI

    init{
        api=AGConnectAPI(AGCredential(CLIENT_ID, CLIENT_SECRET))
    }



    fun getAppInfo(
        appId: String,
        lang: String
    ): String {
        return api.getAppInfo(appId,lang).responseBody.toString()
    }

    fun queryAppId(packageName: String): String? {
        return api.queryAppId(packageName).responseBody?.let {
            if(it.has("appids")){
                it.getJSONArray("appids").getJSONObject(0).getString("value")

            } else ""
        }
    }

    /*fun updateBasicInformation(domain:String,clientId: String,token: String,appId: String){
        try{
            val url=URL("$domain/publish/v2/app-info?appId=$appId")
            val conn=url.openConnection() as HttpURLConnection
            conn.requestMethod = "PUT"
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("client_id", clientId)
            conn.setRequestProperty("content-type","application/json; utf-8")
            val content=JSONObject()
            //1 for free 0 for paid
            content.put("isFree", 1)
            content.put("developerEmail","sdf@sdf.com")
            content.put("developerWebsite","")
            uploadJson(content,conn.outputStream)
            val code=conn.responseCode
            if(code==200){
                val result=convertStreamToString(conn.inputStream)
                Log.e("UpdateInfo",result)
                return
            }
            Log.e("GetAppInfo","Code:$code\tMessage:${conn.responseMessage}")
        }catch (e:Exception){

        }
    }*/

    fun appRelease(domain: String, clientId: String, token: String, appId: String) {
        val url = URL("$domain/publish/v2/app-submit?appid=$appId")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "PUT"
        conn.setRequestProperty("Authorization", "Bearer $token")
        conn.setRequestProperty("client_id", clientId)
        val code = conn.responseCode
        if (code == 200) {
            val result = convertStreamToString(conn.inputStream)
            Log.e("AppRelease", result)
            return
        }
        Log.e("GetAppInfo", "Code:$code\tMessage:${conn.responseMessage}")
    }

    fun getUploadURL(
        appId: String,
        suffix: String
    ): JSONObject? {
        return api.getUploadURL(appId,suffix).responseBody

    }

    /*fun uploadFile(domain: String,clientId: String,token: String,appId: String,suffix: String){
        val  jsonObject = getUploadURL(domain, clientId, token, appId, suffix)
        val authCode=jsonObject.getString("authCode")
        val uploadURL=jsonObject.getString("uploadUrl")
        try{
            val post = HttpPost(uploadURL)
            val bin = FileBody(File("F:\\216X216.png"))
            // Construct a POST request.

            // Construct a POST request.
            val reqEntity: HttpEntity = MultipartEntityBuilder.create()
                .addPart("file", bin)
                .addTextBody("authCode", authCode) // 获取的authCode
                .addTextBody("fileCount", "1")
                .addTextBody("parseType", "1")
                .build()
            post.entity = reqEntity
            post.addHeader("accept", "application/json")

            val httpClient = HttpClients.createDefault()
            val httpResponse = httpClient.execute(post)
            val statusCode = httpResponse.statusLine.statusCode
            if (statusCode == HttpStatus.SC_OK) {
                Log.e("Upload",EntityUtils.toString(httpResponse.entity)
                )
                // Obtain the result code.

            }

        }catch (e:Exception){

        }
    }*/

    fun appRequestAccessToken(domain: String, appId: String, clientSecret: String) {
        try {
            val url = URL(domain)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            //conn.doOutput=true
            //conn.doInput=true
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded")
            val msgBody = MessageFormat.format(
                "grant_type={0}&client_secret={1}&client_id={2}",
                "client_credentials", URLEncoder.encode(clientSecret, "UTF-8"), appId
            )
            Log.e("appAT", msgBody)
            uploadData(msgBody, conn.outputStream)
            if (conn.responseCode < 400) {
                val response = convertStreamToString(conn.inputStream)
                Log.e("AppAccessToken", response)
            } else {
                val response = convertStreamToString(conn.errorStream)
                Log.e("AppAccessToken", response)
            }
            Log.e("AppAccessToken", conn.responseMessage)
            conn.disconnect()
        } catch (e: Exception) {

        }
    }

    fun getReport(
        appId: String,
        lang: String,
        startTime: String,
        endTime: String,
        filterConditions:HashMap<String,String>
    ): String? {
        return api.getReportUrl(appId,lang,startTime,endTime,filterConditions)
            .responseBody?.getString("fileURL")
    }

    fun uploadData(data: String, os: OutputStream) {
        os.write(data.toByteArray(Charsets.UTF_8))
        os.flush()
    }

    private fun convertStreamToString(input: InputStream): String {
        return BufferedReader(InputStreamReader(input)).use {
            val response = StringBuffer()
            var inputLine = it.readLine()
            while (inputLine != null) {
            response.append(inputLine)
            inputLine = it.readLine()
        }
            it.close()
            response.toString()
        }
    }
}