package com.tcp.netty.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tcp.netty.entity.DistributedTransactionConstant;
import com.tcp.netty.entity.Transaction;
import com.tcp.netty.handler.DistributedTransactionClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DistributedTransactionTCPClient extends Thread {

    private Map<String, Transaction> map = new ConcurrentHashMap<String, Transaction>();

    public static void main(String[] args) throws Exception {
	DistributedTransactionTCPClient client = new DistributedTransactionTCPClient("172.16.7.249", 10010);
	client.connect();
	Transaction t = new Transaction(DistributedTransactionConstant.CREATE_TRANSACTION);
	Thread.sleep(5000);
	client.sendTransaction(t);
	Thread.sleep(5000);
	client.queryTransaction(t);
	Thread.sleep(5000);
	System.out.println(t);
	client.close();
    }

    public DistributedTransactionTCPClient(String ip, int port) {
	this.ip = ip;
	this.port = port;
	bootstrap = new Bootstrap();
	bootstrap.group(group).channel(NioSocketChannel.class).handler(new DistributedTransactionClientHandler(this));
    }

    private EventLoopGroup group = new NioEventLoopGroup();

    private String ip;

    private int port;

    private Bootstrap bootstrap;

    private ChannelFuture channelFuture;

    private Channel channel;

    public void connect() {
	this.start();
    }

    @Override
    public void run() {
	try {
	    channelFuture = bootstrap.connect(ip, port).sync();
	    channel = channelFuture.channel();
	    channel.closeFuture().sync();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	} finally {
	    group.shutdownGracefully();
	}
    }

    public void close() {
	if (channel != null)
	    channel.close();
    }

    /**
     * 添加一个子事务
     * 
     * @param parentTransactionId 父类事务id
     */
    public void sendTransaction(Transaction transaction) {
	try {
	    send(transaction);
	    map.put(transaction.getTransactionId(), transaction);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("add transaction error," + e.getMessage());
	}
    }

    /**
     * 查询事务状态
     */
    public void queryTransaction(Transaction transaction) {
	Transaction qt = new Transaction();
	qt.setTransactionId(transaction.getTransactionId());
	qt.setType(DistributedTransactionConstant.QUERY_TRANSACTION);
	try {
	    send(qt);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("query transaction error," + e.getMessage());
	}
    }

    public Transaction getTransaction(String transactionId) {
	return map.get(transactionId);
    }

    public void send(Transaction transaction) throws Exception {
	if (channel == null)
	    throw new NullPointerException("channel is null");
	if (channel.isActive())
	    channel.writeAndFlush(transaction);
	else
	    throw new RuntimeException("channel not is active");
    }

}
