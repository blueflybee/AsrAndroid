package com.yjcloud.asrsdk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.yjcloud.asr.sdk.util.HttpCaller;
import com.yjcloud.asr.sdk.util.JSONUtil;
import com.yjcloud.asr.sdk.util.UrlConcatUtils;
import com.yjcloud.asr.sdk.vo.ResultInfoVO;

/**
 * 泛热词操作客户端
 * @author wangjq
 *
 */
public class AsrModelClient {

	protected final Log LOG = LogFactory.getLog(getClass());
	
	private HttpCaller httpCaller;
	
	private String modelUrl;
	
	private static final String MODEL_ID_PREFIX = "M_";
	
	private static final String SUBMIT_URL_SUFFIX = "submit";
	
	private static final String MODIFY_URL_SUFFIX = "modify";
	
	private static final String INFO_URL_SUFFIX = "info";
	
	public HttpCaller getHttpCaller() {
		return httpCaller;
	}

	/**
	 * 
	 * @param host 服务地址
	 * @param modelUrl 自定义模型服务URL
	 * @param accesskeyId 
	 * @param accessKeySecret
	 */
	public AsrModelClient(String host, String modelUrl, String accesskeyId, String accessKeySecret) {
		this.modelUrl = modelUrl;
		httpCaller = new HttpCaller(host, accesskeyId, accessKeySecret);
	}

	/**
	 *
	 * @param host
	 * @param token
	 */
	public AsrModelClient(String host,String token){
		//token验证
		this.modelUrl = "";

	}

	/**
	 * 
	 * @param text 语料文本
	 * @return
	 * @throws Exception
	 */
	public ResultInfoVO createModel(String text) throws Exception{
		String id= new18uid();
		return createOrModify(id, UrlConcatUtils.concat(modelUrl, SUBMIT_URL_SUFFIX), text);
	}
	
	/**
	 * 
	 * @param modelId 模型id
	 * @param text 语料文本
	 * @return
	 * @throws Exception
	 */
	public ResultInfoVO modifyModel(String modelId,String text) throws Exception{
		return createOrModify(modelId, UrlConcatUtils.concat(modelUrl, MODIFY_URL_SUFFIX), text);
	}
	
	private ResultInfoVO createOrModify(String id,String url,String text) throws Exception{
		if(LOG.isDebugEnabled()){
			LOG.debug("模型服务地址为:"+url);
		}
		Map<String, Object> param = new HashMap<>();
		param.put("id", id);
		param.put("text", text);
		String body = JSONUtil.obj2json(param);
		HttpResponse httpResp = httpCaller.sendPost(url, body);
		if(httpResp.getStatusLine().getStatusCode() == 200){
			String json = EntityUtils.toString(httpResp.getEntity());
			ResultInfoVO rs = JSONUtil.json2obj(json, ResultInfoVO.class);
			if(rs.isOK()){
				rs.setId(id);
			}
			return rs;
		}else{
			return ResultInfoVO.buildErrorVO("请求模型服务调用异常");
		}
	}
	
	/**
	 * 
	 * @param modelId
	 * @return
	 * @throws Exception
	 */
	public ResultInfoVO fetchModelById(String modelId) throws Exception{
		String infoUrl = UrlConcatUtils.concat(modelUrl, INFO_URL_SUFFIX);
		String url = UrlConcatUtils.concat(infoUrl, modelId);
		if(LOG.isDebugEnabled()){
			LOG.debug("模型服务地址为:"+url);
		}
		//查询的content-type 为 application/json
		HttpResponse httpResp = httpCaller.sendPost(url, null);
		if(httpResp.getStatusLine().getStatusCode() == 200){
			String json = EntityUtils.toString(httpResp.getEntity());
			ResultInfoVO rs = JSONUtil.json2obj(json, ResultInfoVO.class);
			return rs;
		}else{
			return ResultInfoVO.buildErrorVO("请求模型服务调用异常");
		}
	}
	
	private String new18uid(){
		return MODEL_ID_PREFIX + UUID.randomUUID().toString().replaceAll("-", "").substring(16);
	}
	
}
