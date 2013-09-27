package com.assemblr.oneshot;

import com.assemblr.oneshot.scenes.GamePanel;
import javax.swing.JFrame;


public class ShotMain extends JFrame {
    
    public static void main(String[] args) {
        ShotMain main = new ShotMain();
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.getContentPane().add(new GamePanel());
        main.pack();
        main.setVisible(true);
    }
    
}
