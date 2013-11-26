package com.assemblr.arena06.client;

import com.assemblr.arena06.client.menu.Button;
import com.assemblr.arena06.client.menu.ButtonAction;
import com.assemblr.arena06.client.scenes.MenuPanel;
import com.assemblr.arena06.client.scenes.Panel;
import com.assemblr.arena06.client.scenes.PanelChanger;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JFrame;

public class ShotMain extends JFrame {

    public static void main(String[] args) {
        String ipAddress = "localhost";
        int port = 30155;
        String username = "Player";
        
        for (String arg : args) {
            String[] flag = arg.split("=", 2);
            if (flag.length != 2) continue;
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
        
        ShotMain main = new ShotMain();
        PanelChanger pc = new PanelChanger();
        ArrayList<Button> tempButtons = new ArrayList<Button>();
        tempButtons.add(new Button("Test button 1", new ButtonAction() {

            public void buttonPressed(MouseEvent me) {
                System.out.println("you clicked me");
            }
        }));
        
        
        Panel game = new MenuPanel(tempButtons);
        
        main.setTitle("Arena 06");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.getContentPane().add(game);
        main.pack();
        main.setVisible(true);

        game.start();
    }

}
