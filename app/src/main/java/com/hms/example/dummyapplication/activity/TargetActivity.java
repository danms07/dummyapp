package com.hms.example.dummyapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hms.example.dummyapplication.R;

public class TargetActivity extends AppCompatActivity{

    private boolean comesFromAbility=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Target","onCreate");
        setContentView(R.layout.activity_target);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();
        TextView textView=findViewById(R.id.targetText);

        if(appLinkData!=null){
            String origin =appLinkData.getQueryParameter("origin");
            if(origin!=null&&!origin.equals("")){
                comesFromAbility=true;
                String message="Hello from"+origin;
                textView.setText(message);
            }
        }
    }
    @Override
    public void onBackPressed() {
        if(comesFromAbility){
            finish();//Finish your activity to return to the Huawei Assistant
        }
        //Handle here your custom logic if you want
    }
}
