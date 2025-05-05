
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
        byte[] bytes = JsonUtil.serializeMessage(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}