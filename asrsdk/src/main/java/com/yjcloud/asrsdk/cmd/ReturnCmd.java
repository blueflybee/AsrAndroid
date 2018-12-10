package com.yjcloud.asrsdk.cmd;

public class ReturnCmd {

	private String command;
	
	private String msgId;
	
	private String success;
	
	private String msg;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public boolean isOK(){
		return "true".equals(this.success);
	}
	
	
}
