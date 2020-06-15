package com.hms.example.dummyapplication.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class RequestBindingTask extends AsyncTask<Void,Void,String> {

    private OnBindingListener listener;

    public RequestBindingTask(OnBindingListener listener) {
        this.listener = listener;
    }

    public static final String SECRET="Wqj51Nnxu1OJNZqRco/VJcAlm1rYNRlBlwXVqDpc";
    @Override
    protected String doInBackground(Void... voids) {
        try{
            URL url=new URL("https://wgtf0hai92.execute-api.us-east-2.amazonaws.com/Release/account/binding/request");
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type","application/json; utf-8");
            conn.setRequestProperty("accessKey","AKIAR2H7OOCH6KCZAXEJ");

            long ts=System.currentTimeMillis();
            byte[] hmacSha256 = null;
            String data=String.valueOf(ts);
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm());
            mac.init(secretKeySpec);
            byte[] hash=mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] base64Byte = Base64.encodeBase64(hash, false);
            String signature= new String(base64Byte, StandardCharsets.UTF_8);

            conn.setRequestProperty("sign",signature);
            conn.setRequestProperty("ts", String.valueOf(ts));

            JSONObject json=new JSONObject(payload);
            json.getJSONObject("header").put("timestamp",String.valueOf(ts));
            json.getJSONObject("inquire").getJSONObject("payload").getJSONObject("grant").put("sign",signature);

            OutputStream os=conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            int code=conn.getResponseCode();
            Log.e("RequestBinding","Code:"+code+"\tMessage: "+conn.getResponseMessage());
            if(code==200){
                String response= ServerUtilities.Companion.convertStreamToString(conn.getInputStream());
                Log.e("RequestBinding","Response:"+response);
                JSONObject jsonObject=new JSONObject(response);
                String deeplink=jsonObject.getJSONObject("reply").getJSONObject("accountLoginAddr").getJSONObject("deepLink").getString("url");
                return deeplink;
            }

            return "";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        listener.onRequestResult(result);
    }

    private final String payload="{\n" +
            "\"version\": \"1.0\",\n" +
            "\"header\": {\n" +
            " \"type\": \"Directive\",\n" +
            " \"timestamp\": \"15869741258\",\n" +
            " \"name\": \"AcceptGrant\",\n" +
            " \"namespace\": \"Authorization\"\n" +
            " },\n" +
            "\"inquire\": {\n" +
            "\"inquireId\": \"efd1f6ca-8fdf-11e8-9eb6-529269fb1459\",\n" +
            " \"payload\": {\n" +
            " \"grant\": {\n" +
            "  \"type\": \"OAuth2.Authorization\",\n" +
            "  \"openId\": \"l\",\n" +
            "\"sign\": \"TZ5uc3v0BQJQ5SIXmf0414O3745B6gkNXYZQPR\",\n" +
            "\"abilityId\": \"7a0af511a91f4591b4efbbaacd8bee60\"\n" +
            " }\n" +
            " }\n" +
            " }\n" +
            "}";

    public interface OnBindingListener{
        void onRequestResult(String link);
    }
}
