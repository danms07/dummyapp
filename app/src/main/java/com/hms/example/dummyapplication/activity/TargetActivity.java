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
        TextView textView=findViewById(R.id.targetText);

        if(appLinkData!=null){
            Log.i("Target",appLinkData.toString());
            name=appLinkData.getQueryParameter("name");
            if(name!=null&&!name.equals("")){

                String message="Hello "+name;
                textView.setText(message);
            }
            //probando con 2 array estaticos
            int[] array1 = {1,2,3,4,5};
            int[] array2 = {2,2,2,2,2,2};

            if(array1.length<array2.length){ //entonces array1 sera el array invertido

                //el array invertido será del tamaño del array inicial mas largo
                int[] arrayinvertido = new int[array2.length];

                //invirtiendo el array
                for (int i = array1.length - 1; i >= 0; i--) {
                    arrayinvertido[array1.length-i-1] = array1[i];
                }

                //mostrando el array invertido
                StringBuilder sb=new StringBuilder();
                for (int value : arrayinvertido) {
                    sb.append(value);
                }
                textView.setText(sb.toString());
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
