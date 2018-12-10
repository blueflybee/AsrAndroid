package com.yjcloud.asrsdk.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.yjcloud.open.Client;
import com.yjcloud.open.Request;
import com.yjcloud.open.constant.ContentType;
import com.yjcloud.open.constant.HttpHeader;

/**
 * 能力开放平台调用者
 *
 * @author wangjq
 */
public class HttpCaller {

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
  public HttpResponse sendPost(String contentType, String path, String body) throws Exception {
    Request request = new Request("POST", host, path, accesskeyId, accessKeySecret, TIMEOUT_MILLS);
    Map<String, String> headMap = new HashMap<String, String>();
    // 设置body 的content-type
    headMap.put(
        HttpHeader.HTTP_HEADER_CONTENT_TYPE,
        contentType
    );
    request.setHeaders(headMap);
    request.setStringBody(body);
    HttpResponse response = Client.execute(request);
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
  public HttpResponse sendPost(String path, String body) throws Exception {
    return sendPost(ContentType.CONTENT_TYPE_JSON, path, body);
  }
}
