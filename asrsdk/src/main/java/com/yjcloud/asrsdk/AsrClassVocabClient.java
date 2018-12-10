package com.yjcloud.asrsdk;

import java.util.HashMap;
import java.util.List;
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
 * 类热词操作客户端
 * @author wangjq
 *
 */
public class AsrClassVocabClient {

	protected final Log LOG = LogFactory.getLog(getClass());
	
	private HttpCaller httpCaller;
	
	private static final String CVOCAB_ID_PREFIX = "C_";
	
	private static final String CREATE_URL_SUFFIX = "create";
	
	private static final String MODIFY_URL_SUFFIX = "modify";
	
	private String classUrl;
	
	public HttpCaller getHttpCaller() {
		return httpCaller;
	}

	/**
	 * 
	 * @param host 服务主机
	 * @param classUrl 类热词服务url
	 * @param accesskeyId
	 * @param accessKeySecret
	 */
	public AsrClassVocabClient(String host,String classUrl,String accesskeyId, String accessKeySecret) {
		httpCaller = new HttpCaller(host, accesskeyId, accessKeySecret);
		this.classUrl = classUrl;
	}

	/**
	 * 
	 * @param personVocabs 人名列表
	 * @param placeVocabs 地名列表
	 * @return
	 * @throws Exception
	 */
	public ResultInfoVO createClassVocab(List<String> personVocabs,List<String> placeVocabs) throws Exception{
		String id = new18uid();
		return createOrModify(id,personVocabs,placeVocabs,UrlConcatUtils.concat(classUrl,CREATE_URL_SUFFIX));
	}
	
	/**
	 * 
	 * @param id 词表id
	 * @param personVocabs 人名列表
	 * @param placeVocabs 地名列表
	 * @return
	 * @throws Exception
	 */
	public ResultInfoVO modifyClassVocab(String id,List<String> personVocabs,List<String> placeVocabs) throws Exception{
		return createOrModify(id,personVocabs,placeVocabs,UrlConcatUtils.concat(classUrl,MODIFY_URL_SUFFIX));
	}
	
	private ResultInfoVO createOrModify(String id,List<String> personVocabs,List<String> placeVocabs,String url) throws Exception{
		if(LOG.isDebugEnabled()){
			LOG.debug("模型服务地址为:"+url);
		}
		Map<String, Object> param = new HashMap<>();
		param.put("id", id);
		param.put("personVocabs", personVocabs);
		param.put("placeVocabs", placeVocabs);
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
			return ResultInfoVO.buildErrorVO("请求热词服务异常");
		}
	}
	
	private String new18uid(){
		return CVOCAB_ID_PREFIX + UUID.randomUUID().toString().replaceAll("-", "").substring(16);
	}
	
}
