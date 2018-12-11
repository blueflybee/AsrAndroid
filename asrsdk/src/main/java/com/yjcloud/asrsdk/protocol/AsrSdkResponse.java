package com.yjcloud.asrsdk.protocol;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * 返回DTO
 *
 * @author wangjq
 */
public class AsrSdkResponse {

  private final String TAG = getClass().getSimpleName();
  /*成功处理*/
  public static int STATUS_CODE_200 = 200;
  /*错误请求*/
  public static int STATUS_CODE_400 = 400;
  /*请求要求身份验证*/
  public static int STATUS_CODE_401 = 401;
  /*服务器拒绝请求*/
  public static int STATUS_CODE_403 = 403;
  /*超出最大并发量*/
  public static int STATUS_CODE_429 = 429;
  /*请求超时*/
  public static int STATUS_CODE_408 = 408;
  /*处理出错*/
  public static int STATUS_CODE_500 = 500;
  /*服务不可用*/
  public static int STATUS_CODE_503 = 503;

  private int status_code;

  private String error_msg;

  private int cno;

  public Result result = null;

  public Result getResult() {
    return result;
  }

  public int getCno() {
    return cno;
  }

  public void setCno(int cno) {
    this.cno = cno;
  }

  public int getStatus_code() {
    return status_code;
  }

  public void setStatus_code(int status_code) {
    this.status_code = status_code;
  }

  public String getError_msg() {
    return error_msg;
  }

  public void setError_msg(String error_msg) {
    this.error_msg = error_msg;
  }

  public void loadFromMap(Map<String, Object> map) {
    if (map.containsKey("status_code")) {
      this.status_code = (int) map.get("status_code");
    }
    if (map.containsKey("cno")) {
      this.cno = (int) map.get("cno");
    }
    this.result = new Result();
    if (map.containsKey("result")) {
      Map<String, Object> resultMap = (Map<String, Object>) map.get("result");
      if (resultMap.containsKey("status_code")) {
        this.result.setStatus_code((int) resultMap.get("status_code"));
      }
      if (resultMap.containsKey("begin_time")) {
        this.result.setBegin_time((int) resultMap.get("begin_time"));
      }
      if (resultMap.containsKey("end_time")) {
        this.result.setEnd_time((int) resultMap.get("end_time"));
      }
      if (resultMap.containsKey("sentence_id")) {
        this.result.setSentence_id((int) resultMap.get("sentence_id"));
      }
      if (resultMap.containsKey("text")) {
        String txt = (String) resultMap.get("text");
        try {
          String decodetxt = new String(Base64.decode(txt.getBytes(), Base64.DEFAULT), "utf-8");
          this.result.setText(decodetxt);
        } catch (UnsupportedEncodingException e) {
          Log.w(TAG, "识别结果decode异常,异常原因:{}" + e.getMessage());
          e.printStackTrace();
        }
      }
    }

  }

  public class Result {

    private int status_code;
    private int begin_time;
    private int end_time;
    private int sentence_id;
    private String text;

    public Result() {
    }

    public int getSentenceStatusCode() {
      return this.status_code;
    }

    public String getText() {
      return this.text;
    }

    public int getBegin_time() {
      return this.begin_time;
    }

    public int getEnd_time() {
      return this.end_time;
    }

    public int getSentence_id() {
      return this.sentence_id;
    }

    public void setStatus_code(int status_code) {
      this.status_code = status_code;
    }

    public void setBegin_time(int begin_time) {
      this.begin_time = begin_time;
    }

    public void setEnd_time(int end_time) {
      this.end_time = end_time;
    }

    public void setSentence_id(int sentence_id) {
      this.sentence_id = sentence_id;
    }

    public void setText(String text) {
      this.text = text;
    }

    public int getStatus_code() {
      return this.status_code;
    }

    @Override
    public String toString() {
      return "Result{" +
          "status_code=" + status_code +
          ", begin_time=" + begin_time +
          ", end_time=" + end_time +
          ", sentence_id=" + sentence_id +
          ", text='" + text + '\'' +
          '}';
    }
  }

  @Override
  public String toString() {
    return "AsrSdkResponse{" +
        "status_code=" + status_code +
        ", error_msg='" + error_msg + '\'' +
        ", cno=" + cno +
        ", result=" + result +
        '}';
  }
}
