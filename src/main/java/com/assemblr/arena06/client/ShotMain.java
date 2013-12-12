package com.assemblr.arena06.client;

import com.assemblr.arena06.client.scene.GameScene;
import com.assemblr.arena06.client.scene.MenuScene;
import com.assemblr.arena06.client.scene.NavigationController;
import com.assemblr.arena06.client.scene.Scene;
import java.util.Deque;
import java.util.LinkedList;
import javax.swing.JFrame;

public class ShotMain extends JFrame implements NavigationController {
    
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
        if (args.length > 1)
            main.pushScene(new GameScene(ipAddress, port, username));
        else
            main.pushScene(new MenuScene());
        main.pack();
        main.setSize(700, 700);
        main.setVisible(true);
    }
    
    private final Deque<Scene> scenes = new LinkedList<Scene>();
    
    public void pushScene(Scene scene) {
        Scene old = scenes.peek();
        scenes.push(scene);
        
        scene.setNavigationController(this);
        if (old != null)
            old.sceneWillDisappear();
        scene.sceneWillAppear();
        
        if (old != null)
            remove(old);
        add(scene);
        revalidate();
        
        if (old != null)
            old.sceneDidDisappear();
        scene.sceneDidAppear();
    }
    
    public Scene popScene() {
        Scene old = scenes.pop();
        Scene scene = scenes.peek();
        
        old.sceneWillDisappear();
        scene.sceneWillAppear();
        
        remove(old);
        add(scene);
        revalidate();
        
        old.sceneDidDisappear();
        scene.sceneDidAppear();
        old.setNavigationController(null);
        
        return old;
    }
    
    public Scene replaceScene(Scene scene) {
        Scene old = scenes.pop();
        scenes.push(scene);
        
        scene.setNavigationController(this);
        old.sceneWillDisappear();
        scene.sceneWillAppear();
        
        remove(old);
        add(scene);
        revalidate();
        
        old.sceneDidDisappear();
        scene.sceneDidAppear();
        old.setNavigationController(null);
        
        return old;
    }
    
}
