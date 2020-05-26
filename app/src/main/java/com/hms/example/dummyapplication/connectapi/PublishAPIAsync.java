package com.hms.example.dummyapplication.connectapi;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PublishAPIAsync extends AsyncTask <Void,Void,Integer> {

    private static final String CLIENT_ID="345768629914042368";
    private static final String CLIENT_SECRET="7F1F2032934D4521CC64F414E7797830DEA023460E6C1B4BC76CF0649D6F8F93";
    private static final String DOMAIN="https://connect-api.cloud.huawei.com/api";



    @Override
    protected Integer doInBackground(Void... voids) {
        ConnectApiHelper helper=new ConnectApiHelper();
        helper.appRequestAccessToken("https://oauth-login.cloud.huawei.com/oauth2/v2/token","101986057","4fad7c177c230047610a01f8f5bc7584d10f7c32948f2baf7eae5d1dc6da0206");
        String token=helper.getToken(DOMAIN,CLIENT_ID,CLIENT_SECRET);
        Log.e("GetToken",token);
        String appId=helper.queryAppId(DOMAIN,CLIENT_ID,token,"com.hms.example.dummyapplication");
        helper.getAppInfo(DOMAIN,CLIENT_ID,token,appId,"en-US");
        helper.getUploadURL(DOMAIN,CLIENT_ID,token,appId,"apk");
        List<String> filterCondition=new ArrayList<>(16);
        List<String> filterConditionValue=new ArrayList<>(16);
        filterCondition.add("countryId");
        filterConditionValue.add("MX");
        helper.getReport(DOMAIN,CLIENT_ID,token,appId,"en-US","20200415","20200422",filterCondition,filterConditionValue);
        return 1;

    }

}
