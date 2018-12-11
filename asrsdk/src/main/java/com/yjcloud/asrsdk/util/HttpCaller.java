package com.yjcloud.asrsdk.util;

import android.util.Log;

import com.yjcloud.asrsdk.protocol.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;


/**
 * 能力开放平台调用者
 *
 * @author wangjq
 */
public class HttpCaller {

  public static final String CONTENT_TYPE_JSON = "application/json";
  private String host;

  private String accesskeyId;

  private String accessKeySecret;

  private static final int TIMEOUT_MILLS = 5000;


  public HttpCaller(String host, String accesskeyId, String accessKeySecret) {
    this.host = host;
    this.accesskeyId = accesskeyId;
    this.accessKeySecret = accessKeySecret;
  }

  /**
   * @param contentType
   * @param body
   * @return
   * @throws Exception
   */
  public HttpResponse sendPost(String contentType, String path, String body) {
    HttpResponse response = new HttpResponse();
    BufferedReader reader = null;
    HttpURLConnection connection = null;
    try {
      String urlPath = UrlConcatUtils.formatUrl(host, path, null);
      Log.d("HttpCaller", "urlPath = " + urlPath);
      URL url = new URL(urlPath);
      connection = (HttpURLConnection) url.openConnection();

//    Request request = new Request("POST", host, path, accesskeyId, accessKeySecret, TIMEOUT_MILLS);
      connection.setConnectTimeout(TIMEOUT_MILLS);
      // 设置允许输出
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setDoInput(true);
      // 设置contentType
      connection.setRequestProperty("Content-Type", contentType);
//    connection.setRequestProperty("Connection", "Keep-Alive");
      connection.setRequestProperty("Charset", "UTF-8");
      // 设置接收类型否则返回415错误，待定
      //connection.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
//    connection.setRequestProperty("accept","application/json");

      //设置其他参数
      connection.setRequestProperty("Open-X-Ca-Timestamp", String.valueOf(new Date().getTime()));
      connection.setRequestProperty("Open-X-Ca-Nonce", UUID.randomUUID().toString());
      connection.setRequestProperty("Open-X-Ca-Key", accesskeyId);
      connection.setRequestProperty("Open-X-Ca-Signature", accessKeySecret);

      // 往服务器里面发送数据
      byte[] bytes = body.getBytes();
      // 设置文件长度
      connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
      OutputStream outputStream = connection.getOutputStream();
      outputStream.write(bytes);
      outputStream.flush();
      outputStream.close();

      response.setCode(connection.getResponseCode());
      reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      response.setMsg(reader.readLine());

      Log.d("HttpCaller", "doJsonPost: response = " + response);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (connection != null) {
        connection.disconnect();
      }
    }
    return response;
  }


  /**
   * content-type 默认为 application/json
   *
   * @param path
   * @param body
   * @return
   * @throws Exception
   */

  public HttpResponse sendPost(String path, String body) {
    return sendPost(CONTENT_TYPE_JSON, path, body);
  }


}
