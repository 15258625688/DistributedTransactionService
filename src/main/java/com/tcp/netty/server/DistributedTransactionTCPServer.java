package com.tcp.netty.server;

import com.tcp.netty.handler.DistributedTransactionServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DistributedTransactionTCPServer extends Thread {

    public static void main(String[] args) {
	DistributedTransactionTCPServer service = new DistributedTransactionTCPServer(10010);
	service.openService();
    }

    public DistributedTransactionTCPServer(int port) {
	this.port = port;
	serverBootstrap = new ServerBootstrap();
	serverBootstrap.group(boosGroup, workGroup).channel(NioServerSocketChannel.class)
		.childHandler(new DistributedTransactionServerHandler());
    }

    private int port;

    private EventLoopGroup boosGroup = new NioEventLoopGroup();

    private EventLoopGroup workGroup = new NioEventLoopGroup();

    private ServerBootstrap serverBootstrap;

    private ChannelFuture channelFuture;

    private Channel channel;

    public void openService() {
	this.start();
    }

    @Override
    public void run() {
	try {
	    channelFuture = serverBootstrap.bind(port).sync();
	    channel = channelFuture.channel();
	    channel.closeFuture().sync();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	} finally {
	    boosGroup.shutdownGracefully();
	    workGroup.shutdownGracefully();
	}
    }

    public void close() {
	if (channel != null)
	    channel.close();
    }
}
