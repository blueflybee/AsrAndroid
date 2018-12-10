package com.yjcloud.asrsdk.cmd;

public class PongCmd {

	private String command;
	
	private String msgId;
	
	public PongCmd() {
		command = "PONG";
	}

	public String getCommand() {
		return command;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
	
}
