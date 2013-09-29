package com.assemblr.oneshot;

import com.assemblr.oneshot.scenes.GamePanel;
import javax.swing.JFrame;


public class ShotMain extends JFrame {
    
    public static void main(String[] args) {
        ShotMain main = new ShotMain();
        GamePanel game = new GamePanel();
        
        main.setTitle("Arena Game");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.getContentPane().add(game);
        main.pack();
        main.setVisible(true);
        
        game.start();
    }
    
}
