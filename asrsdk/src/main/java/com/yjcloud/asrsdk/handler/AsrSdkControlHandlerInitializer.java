package com.yjcloud.asrsdk.handler;



import com.yjcloud.asrsdk.AsrClient;
import com.yjcloud.asrsdk.event.AsrListener;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class AsrSdkControlHandlerInitializer extends ChannelInitializer<SocketChannel> {

	private AsrListener listener;
	
	private AsrClient asrClient;
	
	public AsrSdkControlHandlerInitializer(AsrListener listener, AsrClient asrClient) {
		super();
		this.listener = listener;
		this.asrClient = asrClient;
	}



	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new LengthFieldBasedFrameDecoder(1024*1024, 0, 4, 0, 4));
		p.addLast(new IdleStateHandler(70, 0, 20));
		p.addLast(new HeartbeatHandler(asrClient));
		p.addLast(new AsrSdkControlHandler(listener));
	}

}
