package com.assemblr.arena06.client.net;

import com.assemblr.arena06.common.net.AddressedData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Map;
import java.util.Queue;

public class PacketClientHandler extends SimpleChannelInboundHandler<AddressedData> {
    
    private final Queue<Map<String, Object>> output;
    
    public PacketClientHandler(Queue<Map<String, Object>> output) {
        this.output = output;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedData msg) throws Exception {
        output.add(msg.getData());
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
