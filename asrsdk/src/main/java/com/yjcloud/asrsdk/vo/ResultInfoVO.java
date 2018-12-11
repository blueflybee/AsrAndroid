package com.yjcloud.asrsdk.vo;


import java.io.Serializable;

/**
 * 返回结果的VO父类
 */
public class ResultInfoVO implements Serializable {

  //{"result":"err","msg":"call Ali server error","status":null,"ok":false}

  private static final long serialVersionUID = -5790011512315296761L;

  /**
   * 返回正常 ok
   */
  public static final String OK = "ok";

  /**
   * 返回异常 err
   */
  public static final String ERROR = "err";

  private String ok;

  private String result;

  /**
   * 异常内容
   */
  private String msg;

  /**
   * 模型训练状态
   */
  private String status;

  /**
   * 热词id,模型id
   */
  private String id;


  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public static ResultInfoVO buildOkVO() {
    ResultInfoVO vo = new ResultInfoVO();
    vo.setResult(OK);
    return vo;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOk() {
    return ok;
  }

  public void setOk(String ok) {
    this.ok = ok;
  }

  public static ResultInfoVO buildErrorVO(String errMsg) {
    ResultInfoVO vo = new ResultInfoVO();
    vo.setResult(ERROR);
    vo.setMsg(errMsg);
    return vo;
  }

  public boolean isOK() {
    return OK.equals(this.getResult());
  }

  @Override
  public String toString() {
    return "ResultInfoVO{" +
        "result='" + result + '\'' +
        ", msg='" + msg + '\'' +
        ", status='" + status + '\'' +
        ", id='" + id + '\'' +
        '}';
  }
}
