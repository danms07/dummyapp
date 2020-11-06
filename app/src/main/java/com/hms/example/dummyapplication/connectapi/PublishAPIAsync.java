package com.hms.example.dummyapplication.connectapi;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PublishAPIAsync extends AsyncTask <Void,String,Integer> {


    private static final String DOMAIN="https://connect-api.cloud.huawei.com/api";
    private static final String PACKAGE_NAME="com.hms.demo.hmspushjava";
    //private static final String PACKAGE_NAME="com.hms.example.dummyapplication";
    private static final String INFO_URL="https://connect-api.cloud.huawei.com/api/publish/v2/app-info";

    private OnProgressListener listener;

    public PublishAPIAsync(OnProgressListener listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        ConnectApiHelper helper=new ConnectApiHelper();
        publishProgress("obtaining access token...");
        //helper.appRequestAccessToken("https://oauth-login.cloud.huawei.com/oauth2/v2/token","102405457","5a5ec7e12f2246608b625fc96210c0253e272000ff9b4294fcdd7f0c5ece335a");
        //publishProgress("Access Token: "+token);

        publishProgress("Getting APP ID for: "+PACKAGE_NAME);
        String appId=helper.queryAppId(PACKAGE_NAME);
        publishProgress(appId);

        publishProgress("Getting App Information...");
       if(appId!=null){
           String info=helper.getAppInfo(appId,"en-US");
           publishProgress(info);

           //helper.updateBasicInformation(DOMAIN,CLIENT_ID,token,appId);

           publishProgress("Getting Upload URL...");
           JSONObject url=helper.getUploadURL(appId,"apk");
           publishProgress("Upload URL: "+ url.toString());
           publishProgress("Getting App Report... ");
           HashMap<String,String> filterConditions= new HashMap<>();
           filterConditions.put("countryId","MX");
           String report=helper.getReport(appId,"en-US","20200415","20200612",filterConditions);
           publishProgress("Report: "+report);
       }
        return 1;

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if(listener!=null){
            listener.onUpdate(values[0]);
        }
    }

    public interface OnProgressListener{
        void onUpdate(String entry);
    }
}
