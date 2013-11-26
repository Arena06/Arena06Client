package com.assemblr.arena06.client;

import com.assemblr.arena06.client.navigation.NavigationControler;
import com.assemblr.arena06.client.scenes.GamePanel;
import com.assemblr.arena06.client.scenes.MenuPanel;
import com.assemblr.arena06.client.scenes.Panel;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JFrame;

public class ShotMain extends JFrame implements NavigationControler {

    private Queue<Panel> panels = new LinkedList<Panel>();
    private static ShotMain main;

    public static void main(String[] args) {
        String ipAddress = "localhost";
        int port = 30155;
        String username = "Player";

        for (String arg : args) {
            String[] flag = arg.split("=", 2);
            if (flag.length != 2) {
                continue;
            }
            if (flag[0].equalsIgnoreCase("ip")) {
                ipAddress = flag[1];
                if (ipAddress.contains(":")) {
                    String[] split = ipAddress.split(":");
                    ipAddress = split[0];
                    port = Integer.parseInt(split[1]);
                }
            } else if (flag[0].equalsIgnoreCase("port")) {
                port = Integer.parseInt(flag[1]);
            } else if (flag[0].equals("username")) {
                username = flag[1];
            }
        }

        main = new ShotMain();
        
        
        main.setTitle("Arena 06");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.pushPanel(new GamePanel(ipAddress, port, username, main));
        main.pack();
        main.setSize(500, 500);
        main.setVisible(true);

   }

    public void pushPanel(Panel panel) {
        if (panels.peek() != null) {
            main.remove(panels.peek());
        }
        main.add(panel);
        if (panels.peek() != null) {
            panels.peek().leavingView();
        }
        panel.enteringView();
        panels.add(panel);
        panel.revalidate();

    }

    public void popPanel() {
        panels.poll();
    }

    public void swapCurrentPanel(Panel newPanel) {
        popPanel();
        pushPanel(newPanel);
    }

}
