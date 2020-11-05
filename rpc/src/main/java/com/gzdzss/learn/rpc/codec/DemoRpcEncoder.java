package com.gzdzss.learn.rpc.codec;

import com.gzdzss.learn.rpc.Constants;
import com.gzdzss.learn.rpc.compress.Compressor;
import com.gzdzss.learn.rpc.compress.CompressorFactory;
import com.gzdzss.learn.rpc.protocol.Header;
import com.gzdzss.learn.rpc.protocol.Message;
import com.gzdzss.learn.rpc.serialization.Serialization;
import com.gzdzss.learn.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Andrew
 * @create 2020/11/5 5:34 下午
 */
public class DemoRpcEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        Header header = message.getHeader();
        // 依次序列化消息头中的魔数、版本、附加信息以及消息ID
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getExtraInfo());
        byteBuf.writeLong(header.getMessageId());
        Object content = message.getContent();
        if (Constants.isHeartBeat(header.getExtraInfo())) {
            // 心跳消息，没有消息体，这里写入0
            byteBuf.writeInt(0);
            return;
        }
        // 按照extraInfo部分指定的序列化方式和压缩方式进行处理
        Serialization serialization = SerializationFactory.get(header.getExtraInfo());
        Compressor compressor = CompressorFactory.get(header.getExtraInfo());
        byte[] payload = compressor.compress(serialization.serialize(content));
        // 写入消息体长度
        byteBuf.writeInt(payload.length);
        // 写入消息体
        byteBuf.writeBytes(payload);
    
    }
}
