package com.assemblr.arena06.client.scenes;

import com.assemblr.arena06.client.data.Sprite;
import com.assemblr.arena06.client.data.map.TileType;
import com.assemblr.arena06.client.data.map.generators.MapGenerator;
import com.assemblr.arena06.client.data.map.generators.RoomGenerator;
import com.assemblr.arena06.client.data.sprites.Player;
import com.assemblr.arena06.client.utils.Vector2D;
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
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class GamePanel extends JPanel implements KeyEventDispatcher, KeyListener {
    
    private static final double INPUT_ACCELERATION = 4000;
    private static final double FRICTION_ACCELERATION = 2000;
    private static final double MAXIMUM_VELOCITY = 400;
    
    private Random random = new Random();
    
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
        sprites.put(0, player);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
        addKeyListener(this);
    }
    
    public void start() {
        map = mapGenerator.generateMap();
        paintMap();
        
        while (map[(int) Math.round(player.getPosition().x / MapGenerator.TILE_SIZE)][(int) Math.round(player.getPosition().y / MapGenerator.TILE_SIZE)] != TileType.FLOOR) {
            player.setPosition(new Point2D.Double(random.nextInt(map.length) * MapGenerator.TILE_SIZE, random.nextInt(map[0].length) * MapGenerator.TILE_SIZE));
        }
        
        running = true;
        runner = new Thread(new Runnable() {
            public void run() {
                long lastUpdate = System.currentTimeMillis();
                while (running) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                paintImmediately(0, 0, getWidth(), getHeight());
                            }
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                    
                    long elapsed = System.currentTimeMillis() - lastUpdate;
                    if (elapsed < 16) { // 60 FPS
                        try {
                            Thread.sleep(16 - elapsed);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
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
        mapBuffer = getGraphicsConfiguration().createCompatibleImage(map.length * (int) MapGenerator.TILE_SIZE, (map.length == 0 ? 0 : map[0].length) * (int) MapGenerator.TILE_SIZE);
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
        g.dispose();
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
        
        if (Math.abs(velocity.x) < 0.001) velocity.x = 0;
        if (Math.abs(velocity.y) < 0.001) velocity.y = 0;
        
        // perform collision detection
        double xNew = player.getX() + velocity.x * delta;
        double yNew = player.getY() + velocity.y * delta;
        
        if (velocity.x < 0) {
            int xTile = (int) (xNew / MapGenerator.TILE_SIZE);
            for (int yTile = player.getTileY(); yTile <= (int) ((player.getY() + player.getHeight()) / MapGenerator.TILE_SIZE); yTile++) {
                if (map[xTile][yTile].isSolid()) {
                    xNew = (xTile + 1) * MapGenerator.TILE_SIZE;
                }
            }
        } else if (velocity.x > 0) {
            int xTile = (int) ((xNew + player.getWidth()) / MapGenerator.TILE_SIZE);
            for (int yTile = player.getTileY(); yTile <= (int) ((player.getY() + player.getHeight()) / MapGenerator.TILE_SIZE); yTile++) {
                if (map[xTile][yTile].isSolid()) {
                    xNew = xTile * MapGenerator.TILE_SIZE - player.getWidth() - 0.01;
                }
            }
        }
        
        if (velocity.y < 0) {
            int yTile = (int) (yNew / MapGenerator.TILE_SIZE);
            for (int xTile = player.getTileX(); xTile <= (int) ((player.getX() + player.getWidth()) / MapGenerator.TILE_SIZE); xTile++) {
                if (map[xTile][yTile].isSolid()) {
                    yNew = (yTile + 1) * MapGenerator.TILE_SIZE;
                }
            }
        } else if (velocity.y > 0) {
            int yTile = (int) ((yNew + player.getHeight()) / MapGenerator.TILE_SIZE);
            for (int xTile = player.getTileX(); xTile <= (int) ((player.getX() + player.getWidth()) / MapGenerator.TILE_SIZE); xTile++) {
                if (map[xTile][yTile].isSolid()) {
                    yNew = yTile * MapGenerator.TILE_SIZE - player.getHeight() - 0.01;
                }
            }
        }
        
        player.setX(xNew);
        player.setY(yNew);
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
