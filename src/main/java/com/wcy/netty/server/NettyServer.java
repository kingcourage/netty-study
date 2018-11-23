package com.wcy.netty.server;

import com.wcy.netty.codec.PacketDecoder;
import com.wcy.netty.codec.PacketEncoder;
import com.wcy.netty.codec.Spliter;
import com.wcy.netty.server.handler.AuthHandler;
import com.wcy.netty.server.handler.LoginRequestHandler;
import com.wcy.netty.server.handler.MessageRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new LoginRequestHandler());

                        ch.pipeline().addLast(new AuthHandler());
                        ch.pipeline().addLast(new MessageRequestHandler());
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                })
                //维护map可以自定义添加属性
        .childAttr(AttributeKey.newInstance("clientKey"),"clientValue")
                //设置TCP底层相关属性
        .childOption(ChannelOption.SO_KEEPALIVE,true)
        .childOption(ChannelOption.TCP_NODELAY,true);
        bind(serverBootstrap,8000);
    }


    public static void bind(final ServerBootstrap serverBootstrap,final int port){
        serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("端口["+port+"]绑定成功!");
                }else{
                    System.err.printf("端口"+port+"绑定失败!");
                    bind(serverBootstrap,port+1);
                }
            }
        });
    }
}