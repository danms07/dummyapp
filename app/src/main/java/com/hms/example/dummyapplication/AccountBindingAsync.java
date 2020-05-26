package com.hms.example.dummyapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AccountBindingAsync extends AsyncTask<Void,Void,Integer> {
    private String uid;
    private String openId;
    private OnAccountBindListener listener;

    public AccountBindingAsync(String uid, String openId,OnAccountBindListener listener) {
        this.uid = uid;
        this.openId = openId;
        this.listener=listener;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            URL url=new URL("https://wgtf0hai92.execute-api.us-east-2.amazonaws.com/Release/account/binding/user");
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            JSONObject object=new JSONObject();
            object.put("openId", openId);
            object.put("uid",uid);
            OutputStream os=conn.getOutputStream();
            os.write(object.toString().getBytes());
            os.flush();
            Log.e("AccountBind","Code:"+conn.getResponseCode()+"\tMessage:"+conn.getResponseMessage());
            return conn.getResponseCode();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        listener.onAccountBind(integer);
    }

    public interface OnAccountBindListener{
        void onAccountBind(int result);
    }
}
