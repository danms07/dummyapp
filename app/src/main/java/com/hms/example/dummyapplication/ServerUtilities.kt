package com.hms.example.dummyapplication

import org.json.JSONObject
import java.io.*

public class ServerUtilities {
    companion object{
        fun convertStreamToString(input: InputStream):String{
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

        fun uploadJson(json: JSONObject, os: OutputStream){
            os.write(json.toString().toByteArray())
        }

        fun uploadData(data:String,os: OutputStream){
            os.write(data.toByteArray(Charsets.UTF_8))
            os.flush()
        }
    }
}