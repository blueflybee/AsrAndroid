package com.yjcloud.asrsdk;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.yjcloud.asrsdk.util.Configuration;
import com.yjcloud.asrsdk.util.JSONUtil;
import com.yjcloud.open.Client;
import com.yjcloud.open.Request;
import com.yjcloud.open.constant.ContentType;
import com.yjcloud.open.constant.HttpHeader;

/**
 * 服务信息提供者
 * @author wangjq
 *
 */
public class ServerInfoProvider {

	private final String TAG = getClass().getSimpleName();
	/**
	 * 通过open_sdk鉴权并获取服务节点信息
	 * @param accesskeyId
	 * @param accessKeySecret
	 * @return resultMap
	 * @throws Exception
	 */
	public static Map<String,Object> getsocketInfoWithAuth(String accesskeyId,String accessKeySecret) throws Exception{
		Map<String,Object> resultMap = null; 
		String requestMethod = "POST";
		String host = Configuration.REST_HOST;
		String path = Configuration.REST_URL;
		Request request = new Request(requestMethod,host,path,accesskeyId,accessKeySecret,1000);
		Map<String, String> headMap = new HashMap<String, String>();
		// 设置body 的content-type 
		headMap.put(
			HttpHeader.HTTP_HEADER_CONTENT_TYPE, 
			ContentType.CONTENT_TYPE_JSON
		);
	 	// 消息体
		String body ="{\"lan\":\"0\"}";
		request.setHeaders(headMap);
		request.setStringBody(body);
		try {
			HttpResponse response = Client.execute(request);
			if(response.getStatusLine().getStatusCode() == 200){
				String josn = EntityUtils.toString(response.getEntity());
				
					resultMap = JSONUtil.json2map(josn);
			}
		}catch (Exception e) {
			//LOG.error(e.getMessage(),e);
			throw e;
		}
		return resultMap;
	}
	
	public static void main(String[] args) throws Exception {
		ServerInfoProvider.getsocketInfoWithAuth("abc", "avb");
	}
}
