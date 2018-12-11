package com.yjcloud.asrsdk.demo;


import android.content.Context;
import android.util.Log;

import com.yjcloud.asrsdk.AsrClassVocabClient;
import com.yjcloud.asrsdk.AsrClient;
import com.yjcloud.asrsdk.AsrModelClient;
import com.yjcloud.asrsdk.AsrVocabClient;
import com.yjcloud.asrsdk.R;
import com.yjcloud.asrsdk.event.AsrListener;
import com.yjcloud.asrsdk.protocol.AsrSdkResponse;
import com.yjcloud.asrsdk.vo.ResultInfoVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * v1.2 测试用例
 * sdk 1.2版本增加了对：泛热词，类热词，自定义模型的支持
 *
 * @author wangjq
 */
public class AsrDemoV1_2 implements AsrListener {

  /**
   * 泛热词sdkClient,具备调用泛热词的创建以及修改功能
   */
  private AsrVocabClient vocabClient = new AsrVocabClient(OpenInfo.host, OpenInfo.vocab_path, OpenInfo.ak_id, OpenInfo.ak_secret);
  /**
   * 类热词sdkClient,具备调用类热词的创建以及修改功能
   */
  private AsrClassVocabClient classVocabClient = new AsrClassVocabClient(OpenInfo.host, OpenInfo.cvocab_path, OpenInfo.ak_id, OpenInfo.ak_secret);
  /**
   * 定制模型sdkClient,具备调用定制模型的创建，修改以及模型学习状态查询的功能
   */
  private AsrModelClient modelClient = new AsrModelClient(OpenInfo.host, OpenInfo.model_path, OpenInfo.ak_id, OpenInfo.ak_secret);
  /**
   * 语音识别SDK client 负责发送音频，回传识别结果
   */
  private AsrClient client;

  /**
   * 通道数，该demo是使用的电脑内置麦克的方式，则是单通道模式
   */
  int chcnt = 1;

  boolean flag = false;
//  private AsrDemoV1_2 demo = new AsrDemoV1_2();
  private final String TAG = getClass().getSimpleName();


  /**
   * 采集客户端初始化，需要提前生成好，泛热词，类热词，模型的唯一标识
   *
   * @param vocabId
   * @param classVocabId
   * @param modelId
   * @throws Exception
   */
  public void init(String vocabId, String classVocabId, String modelId) throws Exception {
    Log.i(TAG, "init asr client...");
    client = new AsrClient(OpenInfo.host, OpenInfo.sdk_server_path, vocabId, classVocabId, modelId);
    this.client.init(chcnt, OpenInfo.ak_id, OpenInfo.ak_secret, this);
  }


  @Override
  public void onMessageReceived(AsrSdkResponse response) {
    Log.i(TAG, response.getResult().getText());
    int cno = response.getCno();
    Log.i(TAG, "通道 ：" + cno + "的识别结果为:" + response.getResult().getText());

  }

  @Override
  public void onOperationFailed(AsrSdkResponse response) {
    Log.i(TAG, "Operation is Failed,错误状态码为:" + response.getError_msg());
    System.exit(0);
  }

  @Override
  public void onChannelClosed() {
    Log.i(TAG, "Channel is closed");
    //可能需要发起新的start
  }

  @Override
  public void onOperationSuccess(int operationType) {
    if (0 == operationType) {
      Log.i(TAG, "start is success");
      flag = true;
    } else if (1 == operationType) {
      Log.i(TAG, "stop is success");
    } else if (3 == operationType) {
      Log.i(TAG, "finish is success");
      shutDown();
    }
  }

  public static void main(String[] args) throws Exception {

//    String vocabId = generateVocabId();
//    String classVocabId = generateCVocabId();
//    String modelId = generateModelId();
//
//    //初始化,并传入热词以及模型
//    demo.init(vocabId, classVocabId, modelId);
//
//    //开始
//    demo.start();
//
//    //开始发送音频
//    demo.process(null);
//
//    while (true) {
//      Thread.sleep(10);
//    }

  }

  /**
   * 给识别服务发送开始指令，完成识别前准备
   */
  public void start() {
    Log.i(TAG, "start asr client...");
    this.client.start();
  }

  /**
   * 开始识别任务
   *
   * @throws IOException
   * @throws InterruptedException
   */
  public void process(Context context) throws IOException, InterruptedException {
    //检测是否启动成功
    while (true) {
      if (flag) {
        Log.i(TAG, "start success......");
        break;

      } else {
        Log.i(TAG, "waiting for start success ack......");
        Thread.sleep(50L);
      }
    }

    Log.i(TAG, "open audio file...");
    InputStream fis = null;
    // while(true){
    byte[] b = new byte[8000];
    int len = 0;
    try {
//      fis = new FileInputStream(new File("E:\\asr\\test123.wav"));
      fis = context.getResources().openRawResource(R.raw.test);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (fis != null) {
      List<byte[]> voice = new ArrayList<>();
      /**
       * 多通道，list中放入多个音频byte数组
       */
      while ((len = fis.read(b)) > 0) {
        voice.clear();
        voice.add(b);
        client.sendVoice(voice);
        Thread.sleep(250L);
      }
    }

  }

  /**
   * 停止识别进程
   */
  public void shutDown() {
    Log.i(TAG, "close asr client manually!");
    this.client.close();
    Log.i(TAG, "demo done");
  }


  /**
   * 在构造AsrClient的时候需要先构造模型Id
   *
   * @return modeld
   * @throws Exception
   */
  public String generateModelId() throws Exception {
    // 此处可根据实际的业务获取
    String text = "购房者应当向征信管理部门查询本人的信用报告，确认能否贷款。该条件是个人购房贷款的基础，如果一个人有过不良的信用记录，如逾期不归还信用卡欠款等，且不良记录超过银行相关规定的话，不管其他条件如何，都无法获得贷款。";
    ResultInfoVO rs = modelClient.createModel(text);
    String id = rs.getId();
    return id;
  }

  /**
   * 在构造AsrClient的时候需要先构造classVocabId
   *
   * @return classVocabId
   * @throws Exception
   */
  public String generateCVocabId() throws Exception {
    // 生成类热词
    String[] personArr = {"王白怀"};
    List<String> personVocabs = Arrays.asList(personArr);
    String[] placeArr = {"衢州", "曲阜"};
    List<String> placeVocabs = Arrays.asList(placeArr);
    ResultInfoVO rs = classVocabClient.createClassVocab(personVocabs, placeVocabs);
    String id = rs.getId();
    return id;
  }

  /**
   * 在构造AsrClient的时候需要先构造classVocabId
   *
   * @return vocabId
   * @throws Exception
   */
  public String generateVocabId() throws Exception {
    // 生成泛热词
    String[] wordArr = {"香蕉", "苹果", "栗子"};
    List<String> words = Arrays.asList(wordArr);
    ResultInfoVO rs = vocabClient.createVocab(words);
    String id = rs.getId();
    return id;
  }


}
