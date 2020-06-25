package com.hms.example.dummyapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hms.example.dummyapplication.R;
import com.huawei.agconnect.applinking.AGConnectAppLinking;
import com.huawei.agconnect.applinking.ResolvedLinkData;
import com.huawei.hmf.tasks.OnSuccessListener;

public class TargetActivity extends AppCompatActivity implements OnSuccessListener<ResolvedLinkData> {

    private static final int  DEFINED_CODE=10;
    private static final int REQUEST_CODE_SCAN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Target","onCreate");
        setContentView(R.layout.activity_target);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        String name;
        Uri appLinkData = appLinkIntent.getData();
        if(appLinkData!=null){
            Log.i("Target",appLinkData.toString());
            name=appLinkData.getQueryParameter("name");
            if(name!=null&&!name.equals("")){
                TextView textView=findViewById(R.id.targetText);
                String message="Hello "+name;
                textView.setText(message);
            }

        }

        AGConnectAppLinking.getInstance().getAppLinking(getIntent()).addOnSuccessListener(this).addOnFailureListener(e -> {
        });

    }


    @Override
    public void onSuccess(ResolvedLinkData resolvedLinkData) {
        Log.e("Target",resolvedLinkData.getDeepLink().toString());
    }
}
