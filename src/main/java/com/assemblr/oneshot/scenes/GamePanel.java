package com.assemblr.oneshot.scenes;

import com.assemblr.oneshot.data.Sprite;
import com.assemblr.oneshot.data.sprites.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;


public class GamePanel extends JPanel {
    
    Player player = new Player("assemblr");
    public Map<Integer, Sprite> sprites = new HashMap<Integer, Sprite>();
    
    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        sprites.put(0, player);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // translate camera
        g.translate((getWidth() - player.getWidth()) / 2.0 - player.getX(), (getHeight() - player.getHeight()) / 2.0 - player.getY());
        
        for (Sprite sprite : sprites.values()) {
            g.translate(sprite.getX(), sprite.getY());
            sprite.render(g);
            g.translate(-sprite.getX(), -sprite.getY());
        }
        
    }
    
}
