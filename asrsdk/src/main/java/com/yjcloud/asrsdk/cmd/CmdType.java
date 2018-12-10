package com.yjcloud.asrsdk.cmd;

public enum CmdType {

	START_ACK("START_ACK",0),
	STOP_ACK("STOP_ACK",1),
	PONG("PONG",2),
	FINISH_ACK("FINISH_ACK", 3);

	int type;
	
	String name;

	private CmdType(String name,int type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	

}
