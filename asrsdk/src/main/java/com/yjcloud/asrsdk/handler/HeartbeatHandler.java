package com.yjcloud.asrsdk.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yjcloud.asr.sdk.AsrClient;
import com.yjcloud.asr.sdk.cmd.SenderFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
	
	protected static final Log LOG = LogFactory.getLog(HeartbeatHandler.class);

    private int sendPingMsgCount = 0;

    private AsrClient asrClient;
    
    public HeartbeatHandler(AsrClient asrClient) {
		super();
		this.asrClient = asrClient;
	}

	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object paramObject) throws Exception {
        LOG.info("HeartbeatHandler userEventTriggered");
        if (paramObject instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) paramObject).state();
            if (state == IdleState.ALL_IDLE) {
            	//send ping
            	SenderFactory.sendPing(asrClient);
            	sendPingMsgCount++;
            } else if (state == IdleState.READER_IDLE) {
                if (!ctx.channel().equals(asrClient.getCh()) || sendPingMsgCount < 3){
                	super.userEventTriggered(ctx, paramObject);
                    return;
                }
                LOG.warn("chId: {}, heartbeat no response."+ ctx.channel().id());
                //主动关闭连接
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, paramObject);
        }
    }

    public void clearCount() {
        this.sendPingMsgCount = 0;
    }
}