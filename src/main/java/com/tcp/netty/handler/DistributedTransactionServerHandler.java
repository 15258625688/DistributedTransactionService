package com.tcp.netty.handler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.tcp.netty.entity.DistributedTransactionConstant;
import com.tcp.netty.entity.Transaction;
import com.tcp.netty.handler.decoder.DistributedTransactionDecoder;
import com.tcp.netty.handler.decoder.TransactionDecoder;
import com.tcp.netty.handler.encoder.TransactionEncoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

public class DistributedTransactionServerHandler extends ChannelInitializer<SocketChannel> {

    private static Map<String, Transaction> transactionMap = new ConcurrentHashMap<String, Transaction>();

    static {
	new Timer().schedule(new TimerTask() {

	    @Override
	    public void run() {
		synchronized (transactionMap) {
		    long time = System.currentTimeMillis();
		    for (Entry<String, Transaction> entry : transactionMap.entrySet()) {
			Transaction transaction = entry.getValue();
			if (transaction != null && transaction.getCreateTime().getTime() + 60 * 1000 < time)
			    transactionMap.remove(entry.getKey());
		    }
		}
	    }
	}, 60 * 1000, 60 * 1000);
    }

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
//			    System.out.println(transaction);
			    int type = transaction.getType();
			    String transactionId = transaction.getTransactionId();
			    if (transactionId != null) {
				switch (type) {
				case DistributedTransactionConstant.ADD_TRANSACTION:
				    String parentTransactionId = transaction.getParentTransactionId();
				    if (parentTransactionId != null) {
					Transaction pt = transactionMap.get(parentTransactionId);
					Map<String, Transaction> ctMap = pt.getChildTransactions();
					if (ctMap == null) {
					    ctMap = new ConcurrentHashMap<>();
					    pt.setChildTransactions(ctMap);
					}
					ctMap.put(transactionId, transaction);
					transactionMap.put(transactionId, transaction);
				    }
				    break;
				case DistributedTransactionConstant.CREATE_TRANSACTION:
				    transactionMap.put(transactionId, transaction);
				    break;
				case DistributedTransactionConstant.QUERY_TRANSACTION:
				    Transaction t = transactionMap.get(transactionId);
				    if (t != null) {
					t = updateTransaction(t);
					ctx.channel().writeAndFlush(t);
				    }
				    break;
				}
			    }
			}
		    }
		});

    }

    private static Transaction updateTransaction(Transaction transaction) {
	if (transaction != null && transaction.getChildTransactions() != null) {
	    int status = DistributedTransactionConstant.TRANSACTION_SUCCESS;
	    for (Transaction t : transaction.getChildTransactions().values()) {
		t = updateTransaction(t);
		if (t.getTransactionStatus() == DistributedTransactionConstant.TRANSACTION_ERROR) {
		    transaction.setTransactionStatus(DistributedTransactionConstant.TRANSACTION_ERROR);
		    return transaction;
		} else if (t.getTransactionStatus() == DistributedTransactionConstant.TRANSACTION_WAIT)
		    status = DistributedTransactionConstant.TRANSACTION_WAIT;
	    }
	    transaction.setTransactionStatus(status);
	}
	return transaction;
    }

}
