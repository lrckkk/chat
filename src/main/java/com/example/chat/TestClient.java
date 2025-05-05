package com.example.chat;

import com.example.chat.core.codec.MessageDecoder;
import com.example.chat.core.codec.MessageEncoder;
import com.example.chat.model.Message;
import com.example.chat.protocol.MessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TestClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new MessageDecoder())
                                    .addLast(new MessageEncoder())
                                    .addLast(new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                                            System.out.println("Received: " + msg);
                                        }
                                    });
                        }
                    });

            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();

            // 发送登录请求
            Message loginMsg = new Message();
            loginMsg.setType(MessageType.LOGIN_REQUEST);
            loginMsg.setSender("admin");
            loginMsg.setContent("admin123");
            channel.writeAndFlush(loginMsg);

            // 发送测试消息
            Message chatMsg = new Message();
            chatMsg.setType(MessageType.MESSAGE);
            chatMsg.setSender("admin");
            chatMsg.setRoomId("room1");
            chatMsg.setContent("Hello World");
            channel.writeAndFlush(chatMsg);

            channel.closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }
    }
}