package com.yjcloud.asrsdk.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yjcloud.asr.sdk.AsrClient;
import com.yjcloud.asr.sdk.event.AsrListener;
import com.yjcloud.asr.sdk.protocol.AsrSdkResponse;

public class AsrThreadDemo implements AsrListener,Runnable {

	private AsrClient client;
	
	boolean flag = false;
	
	/** 通道数，该demo是使用的电脑内置麦克的方式，则是单通道模式*/
	int chcnt = 1;
	
	int num;
	
	protected static final Log logger = LogFactory.getLog(AsrDemoV1_2.class);
	
	/** 电脑内置麦克的音频采集任务*/
	private LineAudioTask lineAudioTask = new LineAudioTask();
	
	public AsrThreadDemo(int chcnt,int num) throws IOException {
		super();
		
		
		this.chcnt = chcnt;
	
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
		}
	}


	@Override
	public void run() {
		logger.info("==第 "+num+" 个client初始化");
		client = new AsrClient(OpenInfo.host, OpenInfo.sdk_server_path);
		try {
			this.client.init(chcnt,OpenInfo.ak_id,OpenInfo.ak_secret,this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		client.start();
		// 发送麦克风音频
		lineAudioTask.start();
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