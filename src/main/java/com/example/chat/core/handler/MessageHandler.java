package com.example.chat.core.handler;

import com.example.chat.model.Message;
import com.example.chat.protocol.MessageType;
import com.example.chat.service.ChatRoomService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageHandler extends SimpleChannelInboundHandler<Message> {
    private final ChatRoomService chatRoomService = new ChatRoomService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.println("[服务端] 收到消息: " + msg);
        switch (msg.getType()) {
            case MESSAGE:
                System.out.println("[路由] 将消息路由到房间: " + msg.getRoomId());
                chatRoomService.broadcastMessage(msg);
                break;
            case JOIN_ROOM:
                chatRoomService.joinRoom(msg.getSender(), msg.getRoomId());
                break;
            case LEAVE_ROOM:
                chatRoomService.leaveRoom(msg.getSender(), msg.getRoomId());
                break;
            default:
                ctx.fireChannelRead(msg);
        }
    }
}