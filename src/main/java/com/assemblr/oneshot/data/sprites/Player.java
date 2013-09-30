package com.assemblr.oneshot.data.sprites;

import com.assemblr.oneshot.data.Sprite;
import com.assemblr.oneshot.data.map.generators.MapGenerator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Player extends Sprite {
    
    private String name;
    
    public Player(String name) {
        this.name = name;
        
        width = height = MapGenerator.TILE_SIZE - 10;
    }
    
    public Color getColor() {
        return new Color(name.hashCode());
    }
    
    public void render(Graphics2D g) {
        g.setColor(getColor());
        g.fill(new Rectangle2D.Double(0, 0, width, height));
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
