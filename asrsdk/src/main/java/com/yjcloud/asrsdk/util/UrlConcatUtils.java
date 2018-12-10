package com.yjcloud.asrsdk.util;

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
}
