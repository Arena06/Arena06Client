package com.assemblr.arena06.client;

import com.assemblr.arena06.client.scenes.GamePanel;
import javax.swing.JFrame;


public class ShotMain extends JFrame {
    
    public static void main(String[] args) {
        ShotMain main = new ShotMain();
        GamePanel game = new GamePanel(args.length >= 1 ? args[0] : "Player");
        
        main.setTitle("Arena Game");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.getContentPane().add(game);
        main.pack();
        main.setVisible(true);
        
        game.start();
    }
    
}
