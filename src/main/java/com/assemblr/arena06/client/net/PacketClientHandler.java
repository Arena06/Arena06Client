package com.assemblr.arena06.client.net;

import com.assemblr.arena06.common.net.AddressedData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class PacketClientHandler extends SimpleChannelInboundHandler<AddressedData> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedData msg) throws Exception {
        System.out.println("Data recieved: " + msg.getData());
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
