package com.tcp.netty.handler;

import com.tcp.netty.client.DistributedTransactionTCPClient;
import com.tcp.netty.entity.Transaction;
import com.tcp.netty.handler.decoder.DistributedTransactionDecoder;
import com.tcp.netty.handler.decoder.TransactionDecoder;
import com.tcp.netty.handler.encoder.TransactionEncoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

public class DistributedTransactionClientHandler extends ChannelInitializer<SocketChannel> {

    public DistributedTransactionClientHandler(DistributedTransactionTCPClient client) {
	this.client = client;
    }

    private DistributedTransactionTCPClient client;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
	ch.pipeline().addFirst(new DistributedTransactionDecoder()).addFirst(new TransactionDecoder())
		.addFirst(new TransactionEncoder()).addLast(new SimpleChannelInboundHandler<Transaction>() {

		    @Override
		    public void channelActive(ChannelHandlerContext ctx) throws Exception {
			super.channelActive(ctx);
		    }

		    @Override
		    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			super.channelInactive(ctx);
		    }

		    @Override
		    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			super.exceptionCaught(ctx, cause);
		    }

		    @Override
		    protected void channelRead0(ChannelHandlerContext ctx, Transaction transaction) throws Exception {
			if (transaction != null) {
			    System.out.println(transaction);
			    String transactionId = transaction.getTransactionId();
			    if (transactionId != null) {
				Transaction t = client.getTransaction(transactionId);
				Integer status = transaction.getTransactionStatus();
				if (transaction != null && status != null)
				    t.setTransactionStatus(status);
			    }
			}
		    }
		});

    }

}
