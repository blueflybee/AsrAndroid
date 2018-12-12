package com.yjcloud.asrsdk.demo;


public class OpenInfo {

//	public static String host = "http://121.40.253.20:8080/asr-sdk-server/api/v1/";
	public static String host = "http://192.168.101.201:8882/asr-sdk-server/api/v1/";

	public static String ak_id = "8d01903b580586ae";
	
	public static String ak_secret = "ed27b500f43be91b3298057377fd452a";
	
	public static String vocab_path = "/vocab";
	
	public static String cvocab_path = "/classvocab";
	
	public static String model_path = "/models";
	
	public static String sdk_server_path = "/serverinfo";


	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		OpenInfo.host = host;
	}

	public static String getAk_id() {
		return ak_id;
	}

	public static void setAk_id(String ak_id) {
		OpenInfo.ak_id = ak_id;
	}

	public static String getAk_secret() {
		return ak_secret;
	}

	public static void setAk_secret(String ak_secret) {
		OpenInfo.ak_secret = ak_secret;
	}

	public static String getVocab_path() {
		return vocab_path;
	}

	public static void setVocab_path(String vocab_path) {
		OpenInfo.vocab_path = vocab_path;
	}

	public static String getCvocab_path() {
		return cvocab_path;
	}

	public static void setCvocab_path(String cvocab_path) {
		OpenInfo.cvocab_path = cvocab_path;
	}

	public static String getModel_path() {
		return model_path;
	}

	public static void setModel_path(String model_path) {
		OpenInfo.model_path = model_path;
	}

	public static String getSdk_server_path() {
		return sdk_server_path;
	}

	public static void setSdk_server_path(String sdk_server_path) {
		OpenInfo.sdk_server_path = sdk_server_path;
	}
}
