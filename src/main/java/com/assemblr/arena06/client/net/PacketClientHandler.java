package com.assemblr.arena06.client.net;

import com.assemblr.arena06.common.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class PacketClientHandler extends SimpleChannelInboundHandler<Packet> {
    
    private final Lock readLock;
    private final Condition readCondition;
    private final Queue<Map<String, Object>> output;
    
    public PacketClientHandler(Lock readLock, Condition readCondition, Queue<Map<String, Object>> output) {
        this.readLock = readLock;
        this.readCondition = readCondition;
        this.output = output;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        readLock.lock();
        try {
            output.add(msg.getData());
            readCondition.signalAll();
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
    
}
