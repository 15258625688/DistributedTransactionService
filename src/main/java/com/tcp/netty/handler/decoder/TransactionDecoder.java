package com.tcp.netty.handler.decoder;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.tcp.netty.entity.Transaction;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class TransactionDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
	int length = in.readInt();
	byte[] bs = new byte[length];
	in.readBytes(bs);
	String transaction = new String(bs, "utf-8");
	out.add(JSONObject.parseObject(transaction, Transaction.class));
	System.out.println(ctx.channel().remoteAddress() + ">" + ctx.channel().localAddress() + ":" + transaction);
    }

}
