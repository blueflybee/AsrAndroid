package com.yjcloud.asrsdk.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;

public class JSONUtil {
	
	private static ObjectMapper mapper;
	static {
		// 初始化Mapper对象
		mapper = new ObjectMapper();
		
		// 设置为中国上海时区
		mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		
		// 去掉默认的时间戳格式  
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		// 序列化时忽略空值
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		// 设置Map空值忽略
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		
		// 格式化输出
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		
		// 日期类型输出格式: <yyyy-MM-dd HH:mm:ss>
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		
		// 反序列化时忽略到多余值
		// 使得子类或结构相近的VO类序列化json可以反序列化为父类或其他结构相近的PO
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		// 反序列化允许单引号
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}
	
	/* 私有构造函数，保证其无法实例化
	 * 所有暴露方法均通过静态方法调用实现*/
	private JSONUtil() {}
	
	/**
	 * @Title obj2json
	 * 对象转化为JSON字符串
	 * 
	 * */
	public static <T> String obj2json(T entity) {
		String json = null;
		try {
			json = mapper.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return json;
	}

	/**
	 * @Title obj2str
	 * 对象转换为json字符串，obj2json等价方法
	 * 
	 * @see com.yjcloud.utils.JSONUtil.obj2json(<T>)
	 * 
	 * */
	public static <T> String obj2str(T entity) {
		return obj2json(entity);
	}
	
	/**
	 * @Title obj2jsonBytes
	 * 对象转换为json字节数组
	 * 
	 * */
	public static <T> byte[] obj2jsonBytes(T entity) throws Exception {
		return mapper.writeValueAsBytes(entity);
	}
	
	/**
	 * @Title obj2node
	 * 对象转换为JsonNode
	 * 
	 * */
	public static <T> JsonNode obj2node(T entity) throws Exception {
		return mapper.valueToTree(entity);
	}
	
	/**
	 * @Title write2jsonFile
	 * 将对象转化为json字符串并写入文件
	 * 
	 * */
	public static <T> boolean write2jsonFile(String filepath, T entity) throws Exception {
		File file = new File(filepath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return write2jsonFile(new File(filepath), entity);
	}
	
	/**
	 * @Title write2jsonFile
	 * 将对象转化为json字符串并写入文件
	 * 
	 * */
	public static <T> boolean write2jsonFile(File file, T entity) throws Exception {
		try {
			mapper.writeValue(file, entity);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * @Title json2obj
	 * json字符串转换为对象
	 * 
	 * @param json json字符串
	 * @param type 对象类型
	 * @return T   转换后的对象
	 * */
	public static <T> T json2obj(String json, Class<T> type) throws Exception {
		return mapper.readValue(json, type);
	}
	
	/**
	 * @Title str2obj
	 * json2obj等价方法
	 * 
	 * */
	public static <T> T str2obj(String json, Class<T> type) throws Exception {
		return json2obj(json, type);
	}
	
	/**
	 * @Title json2map
	 * json字符串转化为Map对象
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> json2map(String json) throws Exception {
		return mapper.readValue(json, Map.class);
	}
	
	/**
	 * @Title json2map
	 * json字符串转化为指定类型的Map对象
	 * */
	public static <T> Map<String, T> json2map(String json, Class<T> type) throws Exception {
		return mapper.readValue(json, new TypeReference<Map<String, T>>() {});
	}
	
	/**
	 * @Title map2obj
	 * Map转化为对象
	 * */
	public static <T> T map2obj(
			@SuppressWarnings("rawtypes") Map map,
			Class<T> type) throws Exception {
		return mapper.convertValue(map, type);
	}
	
	/**
	 * @Title parseJSON
	 * 从json格式文件中解析对象
	 * 
	 * */
	public static <T> T parseJSON(File json, Class<T> type) throws Exception {
		return mapper.readValue(json, type);
	}
	
	/**
	 * @Title  parseJSON
	 * 从json文件url中解析对象
	 * 
	 * */
	public static <T> T parseJSON(URL url, Class<T> type) throws Exception {
		return mapper.readValue(url, type);
	}

	/**
	 * @Title json2list
	 * json转换为List
	 * 
	 * @param json  数组格式json字符串
	 * @param T     List中的对象类型
	 * 
	 * @return List 指定对象类型的List
	 * 
	 * */
	public static <T> List<T> json2list(String json, Class<T> T) throws Exception {
		CollectionType type = 
				mapper.getTypeFactory().constructCollectionType(List.class, T);
		return mapper.readValue(json, type);
	}
	
	/** 
	 * @Title str2list
	 * json2list等价方法
	 * */
	public static <T> List<T> str2list(String json, Class<T> T) throws Exception {
		return json2list(json, T);
	}
	
	/**@Title json2node 
	 * 字符串转换为JsonNode节点
	 * */
	public static JsonNode json2node(String json) throws Exception {
		return mapper.readTree(json);
	}
	/**
	 * @Title str2node 
	 * 字符串转换为JsonNode节点，json2node等价方法
	 * */
	public static JsonNode str2node(String json) throws Exception {
		return json2node(json);
	}
	
	/**
	 * @Title objectNode
	 * 创建ObjectNode对象
	 */
	public static ObjectNode objectNode() {
		return JsonNodeFactory.instance.objectNode();
	}
	
	/**
	 * @Title arrayNode
	 * 创建ArrayNode对象
	 * */
	public static ArrayNode arrayNode() {
		return JsonNodeFactory.instance.arrayNode();
	}
	
	public static void main(String[] args) throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("role", "1001");
		map.put("time", "2016-07-07");
		
		Map<String, String> rtnMap = json2map(obj2json(map), String.class);
		System.out.println(rtnMap);
	}
}
