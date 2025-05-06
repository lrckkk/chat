package com.example.chat.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {
    private final int port;//服务器监听的端口号

    public ChatServer(int port) {
        this.port = port;
    }//构造方法，传入端口号

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//接收客户端连接的boss线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();//处理每个连接的读写请求的worker线程组

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)//使用两个线程组
                    .channel(NioServerSocketChannel.class)//基于NIO的服务器通道
                    .childHandler(new ChatServerInitializer());//处理每个新连接的初始化逻辑

            ChannelFuture future = bootstrap.bind(port).sync();//绑定端口
            System.out.println("Server started on port " + port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();//程序退出时清理资源
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ChatServer(8080).start();
    }
}