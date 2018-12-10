package com.yjcloud.asrsdk.cmd;

import android.util.Log;

import com.yjcloud.asrsdk.AsrClient;
import com.yjcloud.asrsdk.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * 发送工厂
 *
 * @author wangjq
 */
public class SenderFactory {

  private final static String TAG = "SenderFactory";
  public static String CMD_START = "START";

  public static String CMD_STOP = "STOP";

  public static String CMD_PING = "PING";

  /*表示指令发送来源于java*/
  public static String FROM_JAVA = "0";
  /*发送给服务端的消息类型为:指令*/
  public static byte TYPE_CMD = 11;
  /*发送给服务端的消息类型为:音频*/
  public static byte TYPE_VOICE = 0;

  /**
   * start指令封装并发送
   *
   * @param chcnt 通道数
   */
  public static void sendStartCmd(int chcnt, AsrClient client) {

    String vocabId = client.getVocabId();
    String classVocabId = client.getClassVocabId();
    String modelId = client.getModelId();

    Map<String, Object> msg = new HashMap<>();
    msg.put("command", CMD_START);
    msg.put("from", FROM_JAVA);
    msg.put("msgId", client.getMsgId());
    msg.put("chcnt", chcnt);
    msg.put("akId", client.getAccesskeyId());

    msg.put("vocabId", vocabId);
    msg.put("classVocabId", classVocabId);
    msg.put("modelId", modelId);


    String json = JSONUtil.obj2json(msg);
    // construct ByteBuf proto
    byte[] msgBuf = json.getBytes();
    int msgLength = msgBuf.length;
    int dataLength = 1 + 2 + msgLength;
    ByteBuf byteBuf = Unpooled.buffer(dataLength);
    byteBuf.writeInt(dataLength);
    byteBuf.writeByte(TYPE_CMD);
    //补齐协议
    byteBuf.writeShort(0);
    byteBuf.writeBytes(msgBuf);
    Log.i(TAG, "sendStartCmd to server......");
    // send ByteBuf message to collector
    Channel ch = client.getCh();
    ch.writeAndFlush(byteBuf);
  }

  /**
   * stop指令封装并发送
   */
  public static void sendStopCmd(AsrClient client) {
    Map<String, Object> msg = new HashMap<>();
    msg.put("command", CMD_STOP);
    msg.put("from", FROM_JAVA);
    msg.put("msgId", client.getMsgId());
    msg.put("akId", client.getAccesskeyId());

    String json = JSONUtil.obj2json(msg);
    // construct ByteBuf proto
    byte[] msgBuf = json.getBytes();
    int msgLength = msgBuf.length;
    int dataLength = 1 + 2 + msgLength;
    ByteBuf byteBuf = Unpooled.buffer(dataLength);
    byteBuf.writeInt(dataLength);
    byteBuf.writeByte(TYPE_CMD);
    //补齐协议
    byteBuf.writeShort(0);
    byteBuf.writeBytes(msgBuf);
    Log.i(TAG, "sendStopCmd to server......");
    // send ByteBuf message to collector
    Channel ch = client.getCh();
    ch.writeAndFlush(byteBuf);
  }

  /**
   * 音频发送
   *
   * @param cno  通道号
   * @param data 音频直接数组
   */
  public static void sendData(int cno, byte[] data, AsrClient client) {
    // construct ByteBuf proto
    int len = data.length;
    int dataLength = 1 + 2 + len;
    ByteBuf byteBuf = Unpooled.buffer(dataLength);
    byteBuf.writeInt(dataLength);
    byteBuf.writeByte(TYPE_VOICE);
    //通道编号
    byteBuf.writeShort(cno);
    byteBuf.writeBytes(data);
    // send ByteBuf message to collector
    Channel ch = client.getCh();
    ch.writeAndFlush(byteBuf);
  }

  /**
   * ping指令
   */
  public static void sendPing(AsrClient client) {
    Map<String, Object> msg = new HashMap<>();
    msg.put("from", FROM_JAVA);
    msg.put("command", CMD_PING);
    msg.put("msgId", client.getMsgId());
    msg.put("akId", client.getAccesskeyId());
    String heartbeatMsg = JSONUtil.obj2json(msg);

    byte[] bytes = heartbeatMsg.getBytes();
    int msgLength = bytes.length;
    int dataLength = 1 + 2 + msgLength;

    ByteBuf byteBuf = Unpooled.buffer(dataLength);
    byteBuf.writeInt(dataLength);
    byteBuf.writeByte(TYPE_CMD);
    //补齐协议
    byteBuf.writeShort(0);
    byteBuf.writeBytes(bytes);
    Log.i(TAG, "sendPing to server......");
    Channel ch = client.getCh();
    ch.writeAndFlush(byteBuf);
  }
}
