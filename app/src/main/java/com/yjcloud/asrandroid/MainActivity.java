package com.yjcloud.asrandroid;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yjcloud.asrsdk.demo.AsrDemoV1_2;
import com.yjcloud.asrsdk.demo.OpenInfo;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    Button button = findViewById(R.id.btn_asr);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AsrTask(getContext()).execute();
      }
    });

  }

  @NonNull
  private MainActivity getContext() {
    return MainActivity.this;
  }

  private static class AsrTask extends AsyncTask<Object, Object, Object> {

    private WeakReference<Context> mContextWeakReference;

    public AsrTask(Context context) {
      mContextWeakReference = new WeakReference<>(context);
    }

    @Override
    protected Object doInBackground(Object... objects) {
      try {
        OpenInfo.setHost("http://192.168.101.201:8882/asr-sdk-server/api/v1/");
        AsrDemoV1_2 demo = new AsrDemoV1_2();
        String vocabId = demo.generateVocabId();
        System.out.println("vocabId = " + vocabId);
        String classVocabId = demo.generateCVocabId();
        System.out.println("classVocabId = " + classVocabId);
        String modelId = demo.generateModelId();
        System.out.println("modelId = " + modelId);

        //初始化,并传入热词以及模型
        //V_b3a8639f6694a0fe
        //C_ba9483e0d640ef98
        //M_9d58deed730b3e46
//        demo.init(null, null, null);
        demo.init(vocabId, classVocabId, modelId);

        //开始
        demo.start();

        //开始发送音频
        demo.process(mContextWeakReference.get());

      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }
  }
}
