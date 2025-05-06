// MessageDecoder.java
package com.example.chat.core.codec;

import com.example.chat.model.Message;
import com.example.chat.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) return;//小于8字节不做处理，这是消息头的长度
        in.markReaderIndex();//标记当前读取位置
        //读取魔数
        int magicNumber = in.readInt();
        if (magicNumber != 0x12345678) {  // 假设魔数是0x12345678
            throw new Exception("Invalid magic number");
        }
        // 读取版本
        in.readByte();
        // 读取消息类型（1字节）
        in.readByte();
        // 读取消息体长度（2字节）
        short bodyLength = in.readShort();//消息长度

        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            return;
        }//判断数据是否完整
        byte[] bytes = new byte[bodyLength];
        in.readBytes(bytes);//读取消息内容
        Message message = JsonUtil.parseMessage(bytes);//解析为对象
        out.add(message);//添加到out列表中，表示解码成功，这个对象将被传递到下一个Handler继续继续处理
    }
}
