package com.yjcloud.asrsdk.demo;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yjcloud.asr.sdk.AsrClassVocabClient;
import com.yjcloud.asr.sdk.AsrClient;
import com.yjcloud.asr.sdk.AsrModelClient;
import com.yjcloud.asr.sdk.AsrVocabClient;
import com.yjcloud.asr.sdk.event.AsrListener;
import com.yjcloud.asr.sdk.protocol.AsrSdkResponse;
import com.yjcloud.asr.sdk.vo.ResultInfoVO;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * v1.2 测试用例
 * sdk 1.2版本增加了对：泛热词，类热词，自定义模型的支持
 * @author wangjq
 *
 */
public class AsrDemoV1_2 implements AsrListener {

	/** 泛热词sdkClient,具备调用泛热词的创建以及修改功能*/
	private static AsrVocabClient vocabClient = new AsrVocabClient(OpenInfo.host, OpenInfo.vocab_path,OpenInfo.ak_id,OpenInfo.ak_secret);
	/** 类热词sdkClient,具备调用类热词的创建以及修改功能*/
	private static AsrClassVocabClient classVocabClient = new AsrClassVocabClient(OpenInfo.host, OpenInfo.cvocab_path,OpenInfo.ak_id,OpenInfo.ak_secret);
	/** 定制模型sdkClient,具备调用定制模型的创建，修改以及模型学习状态查询的功能*/
	private static AsrModelClient modelClient = new AsrModelClient(OpenInfo.host, OpenInfo.model_path,OpenInfo.ak_id,OpenInfo.ak_secret);
	/** 语音识别SDK client 负责发送音频，回传识别结果*/
	private AsrClient client;
	
	/** 通道数，该demo是使用的电脑内置麦克的方式，则是单通道模式*/
	int chcnt = 1;
	
	boolean flag = false;
	private static AsrDemoV1_2 demo = new AsrDemoV1_2();
	protected static final Log logger = LogFactory.getLog(AsrDemoV1_2.class);
	/** 电脑内置麦克的音频采集任务*/
	private LineAudioTask lineAudioTask = new LineAudioTask();
	
	/**
	 * 采集客户端初始化，需要提前生成好，泛热词，类热词，模型的唯一标识
	 * @param vocabId
	 * @param classVocabId
	 * @param modelId
	 * @throws Exception
	 */
	public void init(String vocabId, String classVocabId, String modelId) throws Exception{
	    logger.info("init asr client...");
	    client = new AsrClient(OpenInfo.host, OpenInfo.sdk_server_path, vocabId, classVocabId, modelId);
	    this.client.init(chcnt,OpenInfo.ak_id,OpenInfo.ak_secret,this);
	}

	
	@Override
	public void onMessageReceived(AsrSdkResponse response) {
		logger.info(response);
		int cno = response.getCno();
		logger.info("通道 ："+cno+"的识别结果为:"+response.getResult().getText());

	}

	@Override
	public void onOperationFailed(AsrSdkResponse response) {
		logger.info("Operation is Failed,错误状态码为:"+response.getError_msg());
		System.exit(0);
	}

	@Override
	public void onChannelClosed() {
		logger.info("Channel is closed");
		//可能需要发起新的start
	}

	@Override
	public void onOperationSuccess(int operationType) {
		if(0 == operationType){
			logger.info("start is success");
			flag = true;
		} else if(1 == operationType) {
			logger.info("stop is success");
		} else if (3 == operationType) {
			logger.info("finish is success");
			demo.shutDown();
		}
	}

	public static void main(String[] args) throws Exception {
		
		String vocabId = generateVocabId();
		String classVocabId = generateCVocabId();
		String modelId = generateModelId();
		
		//初始化,并传入热词以及模型
		demo.init(vocabId,classVocabId,modelId);
		
		//开始
		demo.start();
		
		//开始发送音频
		demo.process();

		while (true) {
			Thread.sleep(10);
		}

	}
	
	/**
	 * 给识别服务发送开始指令，完成识别前准备
	 */
	public void start(){
		logger.info("start asr client...");
	    this.client.start();
	}
	  
	/**
	 * 开始识别任务
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void process() throws IOException, InterruptedException{

		// 发送麦克风音频
		lineAudioTask.start();

	}
	
	/**
	 * 停止识别进程
	 */
	public void shutDown(){
		logger.info("close asr client manually!");
	    this.client.close();
	    logger.info("demo done");
	}
	  
	
	/**
	 * 在构造AsrClient的时候需要先构造模型Id
	 * @return modeld
	 * @throws Exception
	 */
	private static String generateModelId() throws Exception {
		// 此处可根据实际的业务获取
		String text = "购房者应当向征信管理部门查询本人的信用报告，确认能否贷款。该条件是个人购房贷款的基础，如果一个人有过不良的信用记录，如逾期不归还信用卡欠款等，且不良记录超过银行相关规定的话，不管其他条件如何，都无法获得贷款。";
		ResultInfoVO rs = modelClient.createModel(text);
		String id = rs.getId();
		return id;
	}

	/**
	 * 在构造AsrClient的时候需要先构造classVocabId
	 * @return classVocabId
	 * @throws Exception
	 */
	private static String generateCVocabId() throws Exception {
		// 生成类热词
		String[] personArr = { "王白怀" };
		List<String> personVocabs = Arrays.asList(personArr);
		String[] placeArr = { "衢州", "曲阜" };
		List<String> placeVocabs = Arrays.asList(placeArr);
		ResultInfoVO rs = classVocabClient.createClassVocab(personVocabs, placeVocabs);
		String id = rs.getId();
		return id;
	}

	/**
	 * 在构造AsrClient的时候需要先构造classVocabId
	 * @return vocabId
	 * @throws Exception
	 */
	private static String generateVocabId() throws Exception{
		// 生成泛热词
		String[] wordArr = { "香蕉", "苹果", "栗子" };
		List<String> words = Arrays.asList(wordArr);
		ResultInfoVO rs = vocabClient.createVocab(words);
		String id = rs.getId();
		return id;
	}

	/**
	 * 电脑内置麦克采集任务，仅供参考
	 * @author wangjq
	 *
	 */
	class LineAudioTask implements Runnable {

		public AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;

		public AudioFormat format = new AudioFormat(encoding,
				16000.0f, 16,
				1, 1 * 16 / 8, 16000.0f, false);

		private final AudioFormat af = format;

		private TargetDataLine tdl;

		private Thread thread;

		public LineAudioTask() {
			try {
				this.tdl = AudioSystem.getTargetDataLine(af);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				tdl.open(af);
			} catch (Exception e) {
			}

			// 如果线路已经在运行则此方法不执行任何操作
			tdl.start();

			int captureBufSize = 8000;
			byte[] captureBuf = new byte[captureBufSize];

			while (!Thread.currentThread().isInterrupted()) {
				tdl.read(captureBuf, 0, captureBufSize);
				this.sendAudio(captureBuf);
				int available = tdl.available();
				if (available <= 0) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
					}
					continue;
				}
			}
		}

		public void start() {
			thread = new Thread(this);
			thread.start();
		}

		private void sendAudio(byte[] audio) {
			List<byte[]> voice = new ArrayList<>();

			voice.clear();
			voice.add(audio);
			client.sendVoice(voice);
		}
	}
}
