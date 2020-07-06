package com.tcp.netty.handler.decoder;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class DistributedTransactionDecoder extends LengthFieldBasedFrameDecoder {

    public DistributedTransactionDecoder() {
	super(1024 * 1024, 0, 4);
    }
}
