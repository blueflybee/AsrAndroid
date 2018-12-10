package com.yjcloud.asrsdk.event;

import com.yjcloud.asrsdk.protocol.AsrSdkResponse;

/**
 * 调用SDK回调抽象接口
 * @author wangjq
 *
 */
public abstract interface AsrListener {

	/**
	 * 主要用于识别结果回调
	 * @param response
	 */
	public abstract void onMessageReceived(AsrSdkResponse response);
	
	/**
	 * 用于失败操作回调
	 * 指令发送失败
	 * 参数异常
	 * @param response
	 */
	public abstract void onOperationFailed(AsrSdkResponse response);
	  
	/**
	 * 通道断开
	 */
	public abstract void onChannelClosed();
	
	/**
	 * 用于反馈开始，结束操作成功，方便调用者判断是否可以开始发送音频了
	 * @param operationType
	 * operationType = 0|1  表示开始成功|结束成功
	 * 
	 */
	public abstract void onOperationSuccess(int operationType);
	
}
