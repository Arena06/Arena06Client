package com.assemblr.arena06.client.net;

import com.assemblr.arena06.common.net.AddressedData;
import com.assemblr.arena06.common.net.DataDecoder;
import com.assemblr.arena06.common.net.DataEncoder;
import com.assemblr.arena06.common.net.PacketDecoder;
import com.assemblr.arena06.common.net.PacketEncoder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
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
        client.sendData(ImmutableMap.<String, Object>of(
                "a", "b",
                "foo", ImmutableMap.<String, Object>of(
                    "bar", ImmutableList.<Object>of(1, 2, 3)
                )));
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
             .channel(NioDatagramChannel.class)
             .option(ChannelOption.SO_BROADCAST, true)
             .handler(new ChannelInitializer<DatagramChannel>() {
                @Override
                protected void initChannel(DatagramChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new PacketEncoder(), new DataEncoder(),
                            new PacketDecoder(), new DataDecoder(),
                            new PacketClientHandler(readLock, readCondition, incomingPackets));
                }
            });
            
            channel = b.bind(0).sync().channel();
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
