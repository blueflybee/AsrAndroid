package com.yjcloud.asrsdk.util;

/**
 * rest api 配置类
 * @author wangjq
 *
 */
public class Configuration {

	
	public static String REST_HOST = null;
	
//	public static String REST_URL = "/api/v1/serverinfo";
	public static String REST_URL = "/asr_sdk_api/serverinfo";

	public static void setREST_HOST(String restHost) {
		REST_HOST = restHost;
	}

	public static void setREST_URL(String restUrl) {
		REST_URL = restUrl;
	}
}
