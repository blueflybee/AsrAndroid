package com.yjcloud.asrsdk;


import com.yjcloud.asrsdk.event.AsrListener;

import java.io.IOException;

/**
 * 公安client入口
 */
public class AsrGaClient extends AsrClient {

  private String token;

  private AsrListener listener;

  private int chcnt;

  /**
   * API主机暴露配置
   *
   * @param restHost
   * @param restUrl
   */
  public AsrGaClient(String restHost, String restUrl) {
    super(restHost, restUrl);
  }


  /**
   * @param chcnt    需要输入音频的通道数
   * @param listener 回调类
   * @throws IOException
   * @throws Exception
   */
  public void init(int chcnt, AsrListener listener, String token) throws IOException {

    this.chcnt = chcnt;
    this.listener = listener;
    //auth校验，token解密
    this.token = token;


  }


}
