package com.tcp.netty.handler.encoder;

import com.alibaba.fastjson.JSONObject;
import com.tcp.netty.entity.Transaction;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TransactionEncoder extends MessageToByteEncoder<Transaction> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Transaction transaction, ByteBuf out) throws Exception {
	String t = JSONObject.toJSONString(transaction);
	byte[] bytes = t.getBytes("utf-8");
	int length = bytes.length;
	out.writeInt(length);
	out.writeBytes(bytes);
	System.out.println(ctx.channel().localAddress() + ">" + ctx.channel().remoteAddress() + ":" + transaction);
    }

}
