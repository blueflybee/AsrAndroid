package com.yjcloud.asrsdk.protocol;

/**
 * <pre>
 *     author : shaojun
 *     e-mail : wusj@qtec.cn
 *     time   : 2018/12/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class HttpResponse {
  private int code;
  private String msg;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "HttpResponse{" +
        "code=" + code +
        ", msg='" + msg + '\'' +
        '}';
  }
}
