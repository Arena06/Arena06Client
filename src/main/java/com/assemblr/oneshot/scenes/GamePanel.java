package com.assemblr.oneshot.scenes;

import com.assemblr.oneshot.data.Sprite;
import com.assemblr.oneshot.data.map.TileType;
import com.assemblr.oneshot.data.map.generators.MapGenerator;
import com.assemblr.oneshot.data.map.generators.RoomGenerator;
import com.assemblr.oneshot.data.sprites.Player;
import com.assemblr.oneshot.utils.Vector2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;


public class GamePanel extends JPanel implements KeyEventDispatcher, KeyListener {
    
    private static final double INPUT_ACCELERATION = 4000;
    private static final double FRICTION_ACCELERATION = 2000;
    private static final double MAXIMUM_VELOCITY = 300;
    
    private Thread runner;
    private boolean running = false;
    
    private Player player = new Player("assemblr");
    private Map<Integer, Sprite> sprites = new HashMap<Integer, Sprite>();
    private MapGenerator mapGenerator = new RoomGenerator();
    private TileType[][] map;
    private BufferedImage mapBuffer;
    
    private Vector2D velocity = new Vector2D();
    
    private Set<Integer> keysDown = new HashSet<Integer>();
    
    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        player.setPosition(new Point2D.Double(200, 200));
        sprites.put(0, player);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
        addKeyListener(this);
    }
    
    public void start() {
        map = mapGenerator.generateMap();
        paintMap();
        
        running = true;
        runner = new Thread(new Runnable() {
            public void run() {
                long lastUpdate = System.currentTimeMillis();
                while (running) {
                    repaint();
                    
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    
                    long now = System.currentTimeMillis();
                    update((now - lastUpdate) / 1000.0);
                    lastUpdate = now;
                }
            }
        });
        runner.start();
    }
    
    public void stop() {
        running = false;
    }
    
    private void paintMap() {
        mapBuffer = new BufferedImage(map.length * (int) MapGenerator.TILE_SIZE, (map.length == 0 ? 0 : map[0].length) * (int) MapGenerator.TILE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = mapBuffer.createGraphics();
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == TileType.NONE) continue;
                switch (map[x][y]) {
                case FLOOR:
                case DOOR:
                    g.setColor(Color.LIGHT_GRAY);
                    break;
                case WALL:
                    g.setColor(Color.DARK_GRAY);
                    break;
                }
                g.fillRect(x * (int) MapGenerator.TILE_SIZE, y * (int) MapGenerator.TILE_SIZE, (int) MapGenerator.TILE_SIZE, (int) MapGenerator.TILE_SIZE);
            }
        }
    }
    
    private void update(double delta) {
        Vector2D acceleration = new Vector2D();
        
        int xInput = (keysDown.contains(KeyEvent.VK_A) ? -1 : 0) + (keysDown.contains(KeyEvent.VK_D) ? 1 : 0);
        int yInput = (keysDown.contains(KeyEvent.VK_W) ? -1 : 0) + (keysDown.contains(KeyEvent.VK_S) ? 1 : 0);
        
        if (xInput == 0 || yInput == 0) {
            acceleration.x = INPUT_ACCELERATION * xInput;
            acceleration.y = INPUT_ACCELERATION * yInput;
        } else {
            acceleration.x = INPUT_ACCELERATION * xInput / Math.sqrt(2);
            acceleration.y = INPUT_ACCELERATION * yInput / Math.sqrt(2);
        }
        
        velocity.add(Vector2D.multiply(acceleration, delta));
        if (Vector2D.length(velocity) > 0) {
            Vector2D friction = Vector2D.scale(velocity, FRICTION_ACCELERATION);
            velocity.subtract(Vector2D.multiply(friction, delta).clamp(Vector2D.length(velocity)));
        }
        velocity.clamp(MAXIMUM_VELOCITY);
        
        player.setX(player.getX() + velocity.x * delta);
        player.setY(player.getY() + velocity.y * delta);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // translate camera
        g.translate((getWidth() - player.getWidth()) / 2.0 - player.getX(), (getHeight() - player.getHeight()) / 2.0 - player.getY());
        
        // draw map
        g.drawImage(mapBuffer, 0, 0, null);
        
        for (Sprite sprite : sprites.values()) {
            g.translate(sprite.getX(), sprite.getY());
            sprite.render(g);
            g.translate(-sprite.getX(), -sprite.getY());
        }
        
    }
    
    public boolean dispatchKeyEvent(KeyEvent ke) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(this, ke);
        return true;
    }
    
    public void keyTyped(KeyEvent ke) {
    }
    
    public void keyPressed(KeyEvent ke) {
        if (keysDown.contains(ke.getKeyCode())) return;
        keysDown.add(ke.getKeyCode());
    }
    
    public void keyReleased(KeyEvent ke) {
        if (!keysDown.contains(ke.getKeyCode())) return;
        keysDown.remove(ke.getKeyCode());
    }
    
}
