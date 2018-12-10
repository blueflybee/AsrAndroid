package com.yjcloud.asrsdk.util;

import java.util.UUID;

/**
 * msgId生成器
 * @author wangjq
 *
 */
public class MsgIdFactory {

	public static String newuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
