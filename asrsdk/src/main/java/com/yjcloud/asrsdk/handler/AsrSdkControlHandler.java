package com.yjcloud.asrsdk.handler;


import android.util.Log;

import com.yjcloud.asrsdk.cmd.CmdType;
import com.yjcloud.asrsdk.cmd.PongCmd;
import com.yjcloud.asrsdk.cmd.ReturnCmd;
import com.yjcloud.asrsdk.event.AsrListener;
import com.yjcloud.asrsdk.protocol.AsrSdkResponse;
import com.yjcloud.asrsdk.util.JSONUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

/**
 * 消息接收处理器
 *
 * @author wangjq
 */
public class AsrSdkControlHandler extends ChannelInboundHandlerAdapter {

  private final String TAG = getClass().getSimpleName();
  private AsrListener listener;

  /*文本消息类型*/
  private static final int TYPE_TXT = 1;
  /*指令消息类型*/
  private static final int TYPE_CMD = 12;

  public AsrSdkControlHandler(AsrListener listener) {
    super();
    this.listener = listener;
  }


  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof ByteBuf) {
      ByteBuf byteBuf = (ByteBuf) msg;
      if (byteBuf.readableBytes() < 4) {
        super.channelRead(ctx, msg);
        return;
      }


      byte type = byteBuf.readByte();
      int dataByteLength = byteBuf.readableBytes();
      byte[] textBytes = new byte[dataByteLength];
      byteBuf.readBytes(textBytes);
      byteBuf.release();
      String msgInfo = new String(textBytes);
      Log.i(TAG, "Received messages from the server,msg type is " + type + ",msgInfo is " + msgInfo);
      /**
       * 文本
       */
      if (type == TYPE_TXT) {
        Map<String, Object> map = JSONUtil.json2map(msgInfo);
        Integer finish = (Integer) map.get("finish");

        if (finish != null && finish == 1) {
          listener.onOperationSuccess(CmdType.FINISH_ACK.getType());
          ctx.close();
          return;
        }

        AsrSdkResponse response = new AsrSdkResponse();
        response.loadFromMap(map);
        listener.onMessageReceived(response);
      }
      /**
       * cmd
       */
      else if (type == TYPE_CMD) {
        Map<String, Object> msgMap = JSONUtil.json2map(msgInfo);
        String commandType = (String) msgMap.get("command");
        //如果是PONG消息
        if (CmdType.PONG.getName().equals(commandType)) {
          PongCmd pongCmd = JSONUtil.json2obj(msgInfo, PongCmd.class);
          ctx.pipeline().get(HeartbeatHandler.class).clearCount();
          return;
        } else {
          ReturnCmd returnCmd = JSONUtil.json2obj(msgInfo, ReturnCmd.class);
          if (returnCmd.isOK()) {
            if (CmdType.START_ACK.getName().equals(commandType)) {
              listener.onOperationSuccess(CmdType.START_ACK.getType());
            } else if (CmdType.STOP_ACK.getName().equals(commandType)) {
              listener.onOperationSuccess(CmdType.STOP_ACK.getType());
            }
          } else {
            //失败的情况
          }


        }
      }

    }
  }


  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    Log.i(TAG, "channelRegistered chId: {}" + ctx.channel().id());
    super.channelRegistered(ctx);
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    Log.w(TAG, "channelUnregistered chId: {}" + ctx.channel().id());
    super.channelUnregistered(ctx);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    Log.i(TAG, "channelActive chId: {}" + ctx.channel().id());
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    Log.w(TAG, "channelInactive chId: {}" + ctx.channel().id());
    /*通知用户，通道不可以*/
    listener.onChannelClosed();
  }

}
