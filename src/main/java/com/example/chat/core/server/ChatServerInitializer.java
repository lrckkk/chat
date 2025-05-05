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
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS))
                .addLast(new MessageDecoder())
                .addLast(new MessageEncoder())
                .addLast(new HeartbeatHandler())
                .addLast(new AuthHandler())
                .addLast(new MessageHandler());
    }
}