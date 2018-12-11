package com.yjcloud.asrsdk;

import java.util.Map;

import com.yjcloud.asrsdk.protocol.HttpResponse;
import com.yjcloud.asrsdk.util.Configuration;
import com.yjcloud.asrsdk.util.HttpCaller;
import com.yjcloud.asrsdk.util.JSONUtil;

/**
 * 服务信息提供者
 *
 * @author wangjq
 */
public class ServerInfoProvider {

  private final String TAG = getClass().getSimpleName();

  /**
   * 通过open_sdk鉴权并获取服务节点信息
   *
   * @param accesskeyId
   * @param accessKeySecret
   * @return resultMap
   * @throws Exception
   */
  public static Map<String, Object> getSocketInfoWithAuth(String accesskeyId, String accessKeySecret) throws Exception {
//      String requestMethod = "POST";
    String host = Configuration.REST_HOST;
    String path = Configuration.REST_URL;
    String body = "{\"lan\":\"0\"}";
    HttpCaller httpCaller = new HttpCaller(host, accesskeyId, accessKeySecret);
    HttpResponse response = httpCaller.sendPost(path, body);
    Map<String, Object> resultMap = null;
    if (response.getCode() == 200) {
      String json = response.getMsg();
      resultMap = JSONUtil.json2map(json);
    }
    return resultMap;
  }

  public static void main(String[] args) throws Exception {
    ServerInfoProvider.getSocketInfoWithAuth("abc", "avb");
  }
}
