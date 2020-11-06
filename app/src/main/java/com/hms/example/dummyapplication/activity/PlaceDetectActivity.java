package com.hms.example.dummyapplication.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.aztec.encoder.Encoder;
import com.hms.example.dummyapplication.R;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.common.MLCoordinate;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzer;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzerSetting;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class PlaceDetectActivity extends AppCompatActivity {

    private static final String TAG ="detectar" ;
    TextView mTextView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detect);
        mTextView=findViewById(R.id.mTextView);
        imageView=findViewById(R.id.imagenId);

        cargarImagen();
    }

    private void cargarImagen() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent,"seleccionar"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri mipath=data.getData();
            imageView.setImageURI(mipath);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bmap = drawable.getBitmap();
            analyzer(bmap);

        }
    }

    private void analyzer(Bitmap bitmap) {
        Toast.makeText(getApplication(), String.format("imgane:%s", bitmap),Toast.LENGTH_SHORT).show();
        Log.e("foto",String.format("imgane:%s", bitmap));
        MLApplication.getInstance().setApiKey("CV6vKDxoaSXKhlopIDAKuRANlut2oSNt66X9V69qtRLcbAhiQ8e8j1I/x3SZsjqmcnQM6vE9+KQVTH+myk9gNrBjTjXE");
        MLRemoteLandmarkAnalyzerSetting settings = new MLRemoteLandmarkAnalyzerSetting.Factory()
                .setLargestNumOfReturns(10) //max num of result
                .setPatternType(MLRemoteLandmarkAnalyzerSetting.STEADY_PATTERN) //1 for steady, 2 for Latest
                .create();
        MLRemoteLandmarkAnalyzer analyzer = MLAnalyzerFactory.getInstance().getRemoteLandmarkAnalyzer(settings);
        MLFrame mlFrame = new MLFrame.Creator().setBitmap(bitmap).create();
        Log.e("foto1", String.valueOf((mlFrame)));
        Task<List<MLRemoteLandmark>> task = analyzer.asyncAnalyseFrame(mlFrame);
        task.addOnSuccessListener(landmarkResults -> {
            Toast.makeText(getApplication(),"si hay lugar",Toast.LENGTH_SHORT).show();

            displaySuccess(landmarkResults.get(0));
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplication(),"no lugar",Toast.LENGTH_SHORT).show();
            Log.e("foto1",e.toString());

            displayFailure(e);
        });
    }

    private static void displayFailure(Exception e) {
        Log.e("error", "error->" +e.getMessage());

    }

    private void displaySuccess(MLRemoteLandmark landmark) {
        String result = "";
        if (landmark.getLandmark() != null) {
            result = "Landmark: " + landmark.getLandmark();
        }
        result += "\nPositions: ";
        if (landmark.getPositionInfos() != null) {
            for (MLCoordinate coordinate : landmark.getPositionInfos()) {
                result += "\nLatitude:" + coordinate.getLat();
                result += "\nLongitude:" + coordinate.getLng();
            }
        }
        mTextView.setText(result);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if (analyzer == null) {
        //     return;
        // }
        //  try {
        //     this.analyzer.stop();
        // } catch (IOException e) {
        //     Log.e(Detectar_lugar.TAG, "Stop failed: " + e.getMessage());
    }

}