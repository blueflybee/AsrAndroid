package com.yjcloud.asrsdk.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import io.netty.util.internal.StringUtil;

public class UrlConcatUtils {

  /**
   * @param url
   * @param other
   * @return
   */
  public static String concat(String url, String other) {
    if (url.substring(url.length() - 1).equals("/")) {
      url = url + other;
    } else {
      url = url + "/" + other;
    }
    return url;
  }

  public static String formatUrl(String host, String path, Map<String, String> querys)
      throws UnsupportedEncodingException {
    StringBuilder sbUrl = new StringBuilder();
    sbUrl.append(host);
    if (!TextUtils.isEmpty(path)) {
      sbUrl.append(path);
    }
    if (null != querys) {
      StringBuilder sbQuery = new StringBuilder();
      for (Map.Entry<String, String> query : querys.entrySet()) {
        if (0 < sbQuery.length()) {
          sbQuery.append("&");
        }
        if ((TextUtils.isEmpty((String) query.getKey())) && (!TextUtils.isEmpty((String) query.getValue()))) {
          sbQuery.append((String) query.getValue());
        }
        if (!TextUtils.isEmpty((String) query.getKey())) {
          sbQuery.append((String) query.getKey());
          if (!TextUtils.isEmpty((String) query.getValue())) {
            sbQuery.append("=");
            sbQuery.append(URLEncoder.encode((String) query.getValue(), "UTF-8"));
          }
        }
      }
      if (0 < sbQuery.length()) {
        sbUrl.append("?").append(sbQuery);
      }
    }
    return sbUrl.toString();
  }
}
