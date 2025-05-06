
// MessageEncoder.java
package com.example.chat.core.codec;

import com.example.chat.model.Message;
import com.example.chat.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] body = JsonUtil.serializeMessage(msg);//序列化message
        // 写魔数（4 字节）
        out.writeInt(0x12345678);

        // 写版本（1 字节）
        out.writeByte(1); // 版本号，可随需要调整

        // 写消息类型（1 字节）
        out.writeByte(msg.getType().ordinal()); // 枚举 MessageType 的 ordinal 作为类型

        // 写消息体长度（2 字节）
        out.writeShort(body.length); // 限制长度在 0~65535 字节

        // 写消息体内容（变长）
        out.writeBytes(body);
    }
}