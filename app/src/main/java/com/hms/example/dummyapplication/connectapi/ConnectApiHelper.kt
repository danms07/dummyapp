package com.hms.example.dummyapplication.connectapi

import android.util.Log
//import org.apache.http.HttpEntity
//import org.apache.http.HttpStatus
//import org.apache.http.client.methods.HttpPost
//import org.apache.http.entity.mime.MultipartEntityBuilder
//import org.apache.http.entity.mime.content.FileBody
//import org.apache.http.impl.client.HttpClients
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.MessageFormat


class ConnectApiHelper (){

    fun getToken(domain:String,clientID:String,clientSecret:String):String{
        try{
            val url=URL("$domain/oauth2/v1/token")
            val conn=url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("content-type","application/json; utf-8")
            val json =JSONObject()
            json.put("client_id", clientID)
            json.put("client_secret", clientSecret)
            json.put("grant_type", "client_credentials")
            uploadJson(json,conn.outputStream)
            val code=conn.responseCode
            val message=conn.responseMessage
            if(code==200){
                val result=convertStreamToString(conn.inputStream)
                val jsonResponse= JSONObject(result)
                return jsonResponse.getString("access_token")
            }
            Log.e("GetToken","Code:$code\t Message: $message")
        }catch (e:Exception){

        }
        return ""
    }

    fun getAppInfo(domain:String,clientId:String,token:String,appId:String,lang:String):String{
        try {
            val url=URL("$domain/publish/v2/app-info?appid=$appId&lang=$lang")
            val conn=url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("client_id", clientId)
            val code=conn.responseCode
            if(code==200){
                val result=convertStreamToString(conn.inputStream)
                Log.e("GetAppInfo",result)
                return result
            }
            Log.e("GetAppInfo","Code:$code\tMessage:${conn.responseMessage}")

        }catch (e:Exception){

        }
        return "Unable to get App Info"
    }

    fun queryAppId(domain:String,clientId:String,token:String,packageName:String):String{
        try{
            val url=URL("$domain/publish/v2/appid-list?packageName=$packageName")
            val conn=url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("client_id", clientId)
            val code=conn.responseCode
            if(code==200){
                val result=convertStreamToString(conn.inputStream)
                Log.e("queryAppId",result)
                val jsonResponse=JSONObject(result)
                val array=jsonResponse.getJSONArray("appids")
                val query=array.getJSONObject(0)
                val appId=query.getString("value")
                return appId
            }
            Log.e("GetAppInfo","Code:$code\tMessage:${conn.responseMessage}")
        }catch (e:Exception){

        }
        return ""
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

    fun appRelease(domain: String,clientId: String,token: String,appId: String){
        val url=URL("$domain/publish/v2/app-submit?appid=$appId")
        val conn=url.openConnection() as HttpURLConnection
        conn.requestMethod = "PUT"
        conn.setRequestProperty("Authorization", "Bearer $token")
        conn.setRequestProperty("client_id", clientId)
        val code=conn.responseCode
        if(code==200){
            val result=convertStreamToString(conn.inputStream)
            Log.e("AppRelease",result)
            return
        }
        Log.e("GetAppInfo","Code:$code\tMessage:${conn.responseMessage}")
    }

    fun getUploadURL(domain:String,clientId:String,token:String,appId:String,suffix:String):JSONObject{
        try{
            val url=URL("$domain/publish/v2/upload-url?appId=$appId&suffix=$suffix")
            val conn=url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("client_id", clientId)
            val code =conn.responseCode
            if(code==200){
                val result=convertStreamToString(conn.inputStream)
                Log.e("GetUploadURL",result)
                return JSONObject(result)
            }
            Log.e("GetAppInfo","Code:$code\tMessage:${conn.responseMessage}")
        }catch (e:Exception){
            Log.e("GetURL",e.toString())
        }
        return JSONObject()

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

    fun appRequestAccessToken(domain:String,appId:String,clientSecret: String){
        try{
            val url=URL(domain)
            val conn=url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            //conn.doOutput=true
            //conn.doInput=true
            conn.setRequestProperty("content-type","application/x-www-form-urlencoded")
            val msgBody = MessageFormat.format("grant_type={0}&client_secret={1}&client_id={2}",
                "client_credentials", URLEncoder.encode(clientSecret, "UTF-8"),appId)
            Log.e("appAT",msgBody)
            uploadData(msgBody,conn.outputStream)
            if(conn.responseCode<400){
                val response=convertStreamToString(conn.inputStream)
                Log.e("AppAccessToken",response)
            }
            else{
                val response=convertStreamToString(conn.errorStream)
                Log.e("AppAccessToken",response)
            }
            Log.e("AppAccessToken",conn.responseMessage)
            conn.disconnect()
        }catch (e:Exception){

        }
    }

    fun getReport(domain:String,clientId: String,token: String,appId: String,lang: String,startTime:String,endTime:String,filterCondition:List<String>,filterConditionValue:List<String>):String{
        val fc = java.lang.StringBuilder()
        for (i in filterCondition.indices) {
            fc.append("&filterCondition=")
            fc.append(filterCondition[i])
            fc.append("&filterConditionValue=")
            fc.append(filterConditionValue[i])
        }
        val sc = fc.toString()

        try{
            val url=URL("$domain/report/distribution-operation-quality/v1/orderDetailExport/$appId?language=$lang&startTime=$startTime&endTime=$endTime$sc")
            val conn=url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("client_id", clientId)
            if(conn.responseCode==200){
                val response=convertStreamToString(conn.inputStream)
                Log.e("getReport",response)
                return response
            }

        }catch (e:Exception){

        }
        return "Unable to get the report"
    }

    fun uploadJson(json:JSONObject,os:OutputStream){
        os.write(json.toString().toByteArray())
    }

    fun uploadData(data:String,os:OutputStream){
        os.write(data.toByteArray(Charsets.UTF_8))
        os.flush()
    }

    fun convertStreamToString(input:InputStream):String{
        val reader = BufferedReader(InputStreamReader(input))
        val sb = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }


}