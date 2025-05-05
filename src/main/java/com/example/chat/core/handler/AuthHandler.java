package com.example.chat.core.handler;

import com.example.chat.model.Message;
import com.example.chat.model.User;
import com.example.chat.protocol.MessageType;
import com.example.chat.protocol.StatusCode;
import com.example.chat.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthHandler extends SimpleChannelInboundHandler<Message> {
    private final UserService userService = new UserService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg.getType() != MessageType.LOGIN_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }

        User user = userService.authenticate(msg.getSender(), msg.getContent());
        if (user != null) {
            Message response = new Message();
            response.setType(MessageType.LOGIN_RESPONSE);
            response.setStatus(StatusCode.SUCCESS);
            response.setContent("Login successful");
            response.setSender("system");
            ctx.writeAndFlush(response);
            ctx.channel().attr(UserService.USER_KEY).set(user);
            userService.addOnlineUser(user, ctx.channel());
        } else {
            Message response = new Message();
            response.setType(MessageType.LOGIN_RESPONSE);
            response.setStatus(StatusCode.UNAUTHORIZED);
            response.setContent("Invalid credentials");
            response.setSender("system");
            ctx.writeAndFlush(response);
            ctx.close();
        }
    }
}