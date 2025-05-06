package com.example.chat.core.server;

import com.example.chat.core.codec.MessageDecoder;
import com.example.chat.core.codec.MessageEncoder;
import com.example.chat.core.handler.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
                .addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS))
                .addLast(new MessageDecoder())
                .addLast(new MessageEncoder())
                .addLast(new HeartbeatHandler())
                .addLast(new AuthHandler())
                .addLast(new MessageHandler());
    }
}
//Netty 服务器端的 通道初始化器，用于配置每个新建立的客户端连接应该使用哪些处理器（Handler）
//这段代码配置了每个连接进来后，服务器如何一步一步地处理收到的数据