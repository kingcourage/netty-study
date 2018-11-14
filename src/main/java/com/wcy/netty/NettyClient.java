package com.wcy.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final Integer MAX_RETRY = 5;
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.
                //指定线程模型
                group(group).
                //指定IO类型为NIO
                channel(NioSocketChannel.class)
                //IO逻辑处理
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new FirstClientHandler());                    }
                });
       connect(bootstrap,"127.0.0.1",1000,MAX_RETRY);

    }

    private static void connect(Bootstrap bootstrap,String host,int port,int retry){

        bootstrap.connect(host,port).addListener(future -> {
            if(future.isSuccess()){
                System.out.println("连接成功");
            }else if(retry == 0){
                System.err.println("重试次数已经用完，放弃连接！");
            }else{
                //第几次重连
                int order = (MAX_RETRY-retry)+1;

                //重连间隔
                int delay = 1 << order;
                System.err.println(new Date()+":连接失败，第"+order+"次重连。。。。");
                bootstrap.config().group().schedule(()->
                    connect(bootstrap,host,port,retry-1),delay, TimeUnit.SECONDS);
                }
        });
    }
}
