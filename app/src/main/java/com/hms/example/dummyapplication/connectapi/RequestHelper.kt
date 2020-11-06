package com.hms.example.dummyapplication.connectapi

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class ResponseObject(val code:Int,val message: String,var responseBody:JSONObject?)

class RequestHelper {
    companion object{
        fun sendRequest(host:String, headers: HashMap<String,String>?, body:JSONObject?, requestType:String="GET"):ResponseObject{
            try {
                val conn = URL(host)
                    .openConnection() as HttpURLConnection
                conn.apply {
                    requestMethod = requestType
                    headers?.apply {
                        for(key in keys)
                            setRequestProperty(key, get(key))
                    }
                    doOutput = requestType == "POST"
                    doInput = true
                }
                if(requestType!="GET"){
                    conn.outputStream.let{
                        body?.apply { it.write(toString().toByteArray()) }
                    }
                }
                val result = when (conn.responseCode) {
                    in 0..300 -> convertStreamToString(conn.inputStream)
                    else -> convertStreamToString(conn.errorStream)
                }
                //Returns the access token, or an empty String if something fails
                return ResponseObject(conn.responseCode,conn.responseMessage, JSONObject(result)).also { conn.disconnect() }
            } catch (e: Exception) {
                return ResponseObject(400,e.toString(),null)
            }
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
}