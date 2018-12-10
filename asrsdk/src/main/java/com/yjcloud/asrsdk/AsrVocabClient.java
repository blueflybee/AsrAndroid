package com.yjcloud.asrsdk;

import android.util.Log;

import com.yjcloud.asrsdk.util.HttpCaller;
import com.yjcloud.asrsdk.util.JSONUtil;
import com.yjcloud.asrsdk.util.UrlConcatUtils;
import com.yjcloud.asrsdk.vo.ResultInfoVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * 泛热词操作客户端
 * @author wangjq
 *
 */
public class AsrVocabClient {

	private final String TAG = getClass().getSimpleName();
	
	private HttpCaller httpCaller;
	
	private static final String VOCAB_ID_PREFIX = "V_";
	
	private static final String CREATE_URL_SUFFIX = "create";
	
	private static final String MODIFY_URL_SUFFIX = "modify";
	
	private String vocabUrl;
	
	public HttpCaller getHttpCaller() {
		return httpCaller;
	}
	
	/**
	 * 
	 * @param host 服务地址
	 * @param vocabUrl 泛热词服务URL
	 * @param accesskeyId 
	 * @param accessKeySecret
	 */
	public AsrVocabClient(String host,String vocabUrl, String accesskeyId, String accessKeySecret) {
		this.vocabUrl = vocabUrl;
		httpCaller = new HttpCaller(host, accesskeyId, accessKeySecret);
	}

	/**
	 * 
	 * @param words 词表
	 * @return
	 * @throws Exception 
	 */
	public ResultInfoVO createVocab(List<String> words) throws Exception{
		String id = new18uid();
		return createOrModify(id,words, UrlConcatUtils.concat(vocabUrl,CREATE_URL_SUFFIX));
	}
	
	/**
	 * 
	 * @param id
	 * @param words
	 * @return
	 * @throws Exception
	 */
	public ResultInfoVO modifyVocab(String id,List<String> words) throws Exception{
		return createOrModify(id,words, UrlConcatUtils.concat(vocabUrl,MODIFY_URL_SUFFIX) );
	}
	
	private ResultInfoVO createOrModify(String id,List<String> words,String url) throws Exception{
		Log.d(TAG, "泛热词服务地址为:" + url);
		Map<String, Object> param = new HashMap<>();
		param.put("id", id);
		param.put("words", words);
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
		return VOCAB_ID_PREFIX + UUID.randomUUID().toString().replaceAll("-", "").substring(16);
	}
	
}
