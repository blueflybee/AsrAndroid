package com.yjcloud.asrsdk;

import com.yjcloud.asrsdk.cmd.SenderFactory;
import com.yjcloud.asrsdk.event.AsrListener;
import com.yjcloud.asrsdk.handler.AsrSdkControlHandlerInitializer;
import com.yjcloud.asrsdk.protocol.AsrSdkResponse;
import com.yjcloud.asrsdk.util.Configuration;
import com.yjcloud.asrsdk.util.MsgIdFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class AsrClient {

  private final String TAG = getClass().getSimpleName();
  private String host = null;

  private int port = 8888;

  private Bootstrap bootstrap;

  private ChannelFuture future;

  private String accesskeyId;

  private String msgId;

  private AsrListener listener;

  private int chcnt;

  private Channel ch;

  /**
   * 泛热词id
   */
  private String vocabId;

  /**
   * 类热词id
   */
  private String classVocabId;

  /**
   * 模型id
   */
  private String modelId;

  private EventLoopGroup workerGroup = new NioEventLoopGroup();


  /**
   * API主机暴露配置
   *
   * @param restHost
   */
  public AsrClient(String restHost, String restUrl) {
    Configuration.setREST_HOST(restHost);
    Configuration.setREST_URL(restUrl);
  }

  /**
   * 带热词，自定义模型的构造器
   *
   * @param restHost     获取sdk server的ip
   * @param restUrl      获取sdk server的url
   * @param vocabId      泛热词id
   * @param classVocabId 类热词id
   * @param modelId      模型id
   */
  public AsrClient(String restHost, String restUrl, String vocabId, String classVocabId, String modelId) {
    this(restHost, restUrl);
    this.vocabId = vocabId;
    this.classVocabId = classVocabId;
    this.modelId = modelId;
  }


  /**
   * @param chcnt           需要输入音频的通道数
   * @param accesskeyId
   * @param accessKeySecret
   * @param listener        回调类
   * @throws IOException
   * @throws Exception
   */
  public void init(int chcnt, String accesskeyId, String accessKeySecret, AsrListener listener) throws IOException {

    Map<String, Object> socketInfo;
    try {
      this.listener = listener;
      socketInfo = ServerInfoProvider.getSocketInfoWithAuth(accesskeyId, accessKeySecret);
      this.chcnt = chcnt;
      String hostPort = (String) socketInfo.get("collector");
      try {
        initHostAndPort(hostPort);
      } catch (Exception e) {
        e.printStackTrace();
        AsrSdkResponse response = new AsrSdkResponse();
        response.setStatus_code(AsrSdkResponse.STATUS_CODE_500);
        response.setError_msg("没有可用的ASR识别服务。。。。。。");
        this.listener.onOperationFailed(response);
        return;
      }

			/*构建msgId*/
      String msgId = MsgIdFactory.newuid();
      this.msgId = msgId;
      this.accesskeyId = accesskeyId;
      bootstrap = new Bootstrap();
      bootstrap.group(workerGroup).channel(NioSocketChannel.class);
      bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
      bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
      bootstrap.handler(new AsrSdkControlHandlerInitializer(listener, this));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw e;
    } catch (NullPointerException e) {
      e.printStackTrace();
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      AsrSdkResponse response = new AsrSdkResponse();
      response.setStatus_code(AsrSdkResponse.STATUS_CODE_503);
      response.setError_msg("获取服务地址服务不可用。。。。。。");
      this.listener.onOperationFailed(response);
    }


  }

  /**
   * @param hostPort 主机ipPort
   */
  private void initHostAndPort(String hostPort) {
    String[] strArr = hostPort.split(":");
    if (strArr.length == 2) {
      host = strArr[0];
      port = Integer.parseInt(strArr[1]);
    }
  }

  /**
   * 准备识别
   */
  public void start() {
    boolean successful = false;
    try {
      future = bootstrap.connect(host, port).sync();
      if (future.isSuccess()) {
        successful = true;
      }
    } catch (Exception e) {
      AsrSdkResponse response = new AsrSdkResponse();
      response.setStatus_code(AsrSdkResponse.STATUS_CODE_500);
      this.listener.onOperationFailed(response);
      e.printStackTrace();
      return;
    }
    if (successful) {
      this.ch = future.channel();
      /**
       * 发送start指令
       */
      SenderFactory.sendStartCmd(chcnt, this);
    }

  }

  /**
   * 停止识别
   */
  public void stop() {
    /**
     * 发送stop指令
     */
    SenderFactory.sendStopCmd(this);
  }

  /**
   * 多个通道
   *
   * @param datas
   */
  public void sendVoice(List<byte[]> datas) {
    int i = 0;
    for (byte[] data : datas) {
      sendData(data, i++);
    }
  }

  /**
   * 发送音频数据
   *
   * @param data
   * @param cno  通道号
   */
  private void sendData(byte[] data, int cno) {
    SenderFactory.sendData(cno, data, this);

  }

  public void close() {
    if (workerGroup != null) {
      workerGroup.shutdownGracefully();
    }
  }


  public String getAccesskeyId() {
    return accesskeyId;
  }

  public String getMsgId() {
    return msgId;
  }

  public Channel getCh() {
    return ch;
  }

  public String getVocabId() {
    return vocabId;
  }

  public void setVocabId(String vocabId) {
    this.vocabId = vocabId;
  }

  public String getClassVocabId() {
    return classVocabId;
  }

  public void setClassVocabId(String classVocabId) {
    this.classVocabId = classVocabId;
  }

  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }


}
