package com.example.chat.core.handler;

import com.example.chat.model.Message;
import com.example.chat.model.User;
import com.example.chat.protocol.MessageType;
import com.example.chat.protocol.StatusCode;
import com.example.chat.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.sql.SQLException;

public class AuthHandler extends SimpleChannelInboundHandler<Message> {
    private final UserService userService = new UserService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws SQLException {
        if (msg.getType() != MessageType.LOGIN_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }
        System.out.println("[认证] 收到登录请求: " + msg.getSender());
        //这里实际上sender是username，content是密码
        User user = userService.authenticate(msg.getSender(), msg.getContent());
        System.out.println(user);

        if (user != null) {
            Message response = new Message();
            response.setType(MessageType.LOGIN_RESPONSE);
            response.setStatus(StatusCode.SUCCESS);
            response.setContent("Login successful");
            response.setSender("system");
            response.setReceiver(user.getUsername());
            ctx.writeAndFlush(response);
            ctx.channel().attr(UserService.USER_KEY).set(user);
            System.out.println("[认证] 用户 " + user.getUsername() + " 登录成功");
            userService.addOnlineUser(user, ctx.channel());
            System.out.println("[用户上线] " + user.getUsername() + " -> " );
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