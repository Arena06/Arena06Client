package com.assemblr.arena06.client.net;

import com.assemblr.arena06.common.net.AddressedData;
import com.assemblr.arena06.common.net.DataDecoder;
import com.assemblr.arena06.common.net.DataEncoder;
import com.assemblr.arena06.common.net.PacketDecoder;
import com.assemblr.arena06.common.net.PacketEncoder;
import com.google.common.collect.ImmutableMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PacketClient {
    
    public static void main(String[] args) throws Exception {
        final PacketClient client = new PacketClient(new InetSocketAddress("localhost", 30155));
        Thread clientThread = new Thread(new Runnable() {
            public void run() {
                try {
                    client.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        clientThread.start();
    }
    
    private final InetSocketAddress address;
    private Channel channel;
    
    private final ReentrantLock readLock = new ReentrantLock();
    private final Condition readCondition = readLock.newCondition();
    private final Queue<Map<String, Object>> incomingPackets = new ConcurrentLinkedQueue<Map<String, Object>>();
    
    public PacketClient(InetSocketAddress address) {
        this.address = address;
    }
    
    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             //.option(ChannelOption.SO_BROADCAST, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new LengthFieldPrepender(2),                          new PacketEncoder(), new DataEncoder(),
                            new LengthFieldBasedFrameDecoder(0xFFFF, 0, 2, 0, 2), new PacketDecoder(), new DataDecoder(),
                            new PacketClientHandler(readLock, readCondition, incomingPackets));
                }
            });
            
            channel = b.connect(address).channel();
            channel.closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }
    }
    
    public void handshake() {
        boolean success = false;
        readLock.lock();
        try {
            while (!success) {
                sendData(ImmutableMap.<String, Object>of(
                    "type", "handshake"
                ));
                try {
                    success = readCondition.await(1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            incomingPackets.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            readLock.unlock();
        }
    }
    public void writeStringToChannel(String s) {
        ByteBuf b = channel.alloc().buffer(s.length());
        for (int i = 0; i < s.toCharArray().length; i++) {
            b.writeByte(s.toCharArray()[i]);
        }
        
        channel.writeAndFlush(b);
    }
    public void sendData(Map<String, Object> data) {
        while (channel == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        channel.writeAndFlush(new AddressedData(data, null, address));
    }
    
    public void sendDataBlocking(Map<String, Object> data) {
        while (channel == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        channel.writeAndFlush(new AddressedData(data, null, address)).awaitUninterruptibly();
    }
    
    public Queue<Map<String, Object>> getIncomingPackets() {
        return incomingPackets;
    }
    
}
