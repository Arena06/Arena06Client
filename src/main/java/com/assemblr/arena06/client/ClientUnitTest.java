/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assemblr.arena06.client;

import com.assemblr.arena06.client.net.PacketClient;
import com.google.common.collect.ImmutableMap;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Henry
 */
public class ClientUnitTest {

    final int port = 30155;
    private final PacketClient client;

    public static void main(String[] args) {
        try {
            System.out.println("starting client");
            ClientUnitTest cut = new ClientUnitTest();
            cut.run();
            cut.client.sendData(ImmutableMap.<String, Object>of("testkey", getGarbageStirng(2028)
            ));
                    } catch (Exception ex) {
            Logger.getLogger(ClientUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ClientUnitTest() {
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", port);
        System.out.println("connecting to server at " + serverAddress);
        client = new PacketClient(serverAddress);
    }

    public void run() throws Exception {
        Thread runner = new Thread(new Runnable() {

            public void run() {
                try {
                    client.run();
                    System.out.println("it ran");
                } catch (Exception ex) {
                    Logger.getLogger(ClientUnitTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        runner.start();

    }
    
    private static String getGarbageStirng(int bytes) {
        StringBuilder sb = new StringBuilder(bytes);
        for (int i = 0; i < bytes; i++) {
            sb.append('g');
        }
        return sb.toString();
    }

}
