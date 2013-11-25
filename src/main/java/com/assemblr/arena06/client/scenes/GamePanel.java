package com.assemblr.arena06.client.scenes;

import com.assemblr.arena06.common.data.Sprite;
import com.assemblr.arena06.common.data.map.TileType;
import com.assemblr.arena06.common.data.map.generators.MapGenerator;
import com.assemblr.arena06.common.data.map.generators.RoomGenerator;
import com.assemblr.arena06.common.data.Player;
import com.assemblr.arena06.client.net.PacketClient;
import com.assemblr.arena06.common.utils.Vector2D;
import com.google.common.collect.ImmutableMap;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.javatuples.Pair;


public class GamePanel extends JPanel implements KeyEventDispatcher, KeyListener {
    
    private static final double INPUT_ACCELERATION = 4000;
    private static final double FRICTION_ACCELERATION = 2000;
    private static final double MAXIMUM_VELOCITY = 400;
    
    private static final Font FONT_PRIMARY;
    static {
        Font f = null;
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, GamePanel.class.getResourceAsStream("/minecraft.ttf"));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        FONT_PRIMARY = f;
    }
    
    private final Random random = new Random();
    
    private final PacketClient client;
    
    private Thread runner;
    private Thread keepAlive;
    private boolean running = false;
    
    private int playerId = 0;
    private Player player;
    private Map<Integer, Sprite> sprites = new HashMap<Integer, Sprite>();
    private MapGenerator mapGenerator = new RoomGenerator();
    private TileType[][] map;
    private BufferedImage mapBuffer;
    
    private boolean chatting = false;
    private StringBuilder currentChat = new StringBuilder();
    private TreeSet<Pair<Date, String>> chats = new TreeSet<Pair<Date, String>>();
    
    private Vector2D velocity = new Vector2D();
    
    private Set<Integer> keysDown = new HashSet<Integer>();
    
    public GamePanel(String ipAddress, int port, String username) {
        InetSocketAddress serverAddress = new InetSocketAddress(ipAddress, port);
        System.out.println("connecting to server at " + serverAddress);
        client = new PacketClient(serverAddress);
        player = new Player(username);
        setPreferredSize(new Dimension(800, 600));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
        addKeyListener(this);
        
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
        
        keepAlive = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    client.sendData(ImmutableMap.<String, Object>of(
                        "type", "keep-alive"
                    ));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    
    public void start() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    client.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
        
        System.out.println("handshaking with server...");
        client.handshake();
        
        running = true;
        runner.start();
        keepAlive.start();
        
        client.sendData(ImmutableMap.<String, Object>of(
            "type", "login",
            "data", player.serializeState()
        ));
        System.out.println("logging in...");
        
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                client.sendDataBlocking(ImmutableMap.<String, Object>of(
                    "type", "logout"
                ));
            }
        });
    }
    
    public void stop() {
        running = false;
    }
    
    private void generateMap(long seed) {
        map = mapGenerator.generateMap(seed);
        paintMap();
        
        while (map[(int) Math.round(player.getPosition().x / MapGenerator.TILE_SIZE)][(int) Math.round(player.getPosition().y / MapGenerator.TILE_SIZE)] != TileType.FLOOR) {
            player.setPosition(new Point2D.Double(random.nextInt(map.length) * MapGenerator.TILE_SIZE, random.nextInt(map[0].length) * MapGenerator.TILE_SIZE));
        }
        
        client.sendData(ImmutableMap.<String, Object>of(
            "type", "sprite",
            "action", "update",
            "id", playerId,
            "data", player.serializeState()
        ));
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
        // process inbound packets
        Map<String, Object> packet;
        while ((packet = client.getIncomingPackets().poll()) != null) {
            if (packet.get("type").equals("login")) {
                player.updateState((Map<String, Object>) packet.get("data"));
                playerId = (Integer) packet.get("id");
                generateMap((Long) packet.get("map-seed"));
                client.sendData(ImmutableMap.<String, Object>of(
                    "type", "request",
                    "request", "sprite-list"
                ));
                System.out.println("logged in as " + player.getName());
            } else if (packet.get("type").equals("request")) {
                if (packet.get("request").equals("sprite-list")) {
                    // refresh sprite list
                    sprites.clear();
                    Map<String, Object> spriteList = (Map<String, Object>) packet.get("data");
                    for (Map.Entry<String, Object> entry : spriteList.entrySet()) {
                        int spriteId = Integer.parseInt(entry.getKey());
                        if (spriteId == playerId) continue;
                        List<Object> spriteData = (List<Object>) entry.getValue();
                        try {
                            Class<? extends Sprite> spriteClass = (Class<? extends Sprite>) Class.forName((String) spriteData.get(0));
                            Sprite sprite = spriteClass.newInstance();
                            sprite.updateState((Map<String, Object>) spriteData.get(1));
                            sprites.put(spriteId, sprite);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else if (packet.get("type").equals("sprite")) {
                if (packet.get("action").equals("create")) {
                    int spriteId = (Integer) packet.get("id");
                    List<Object> spriteData = (List<Object>) packet.get("data");
                    try {
                        Class<? extends Sprite> spriteClass = (Class<? extends Sprite>) Class.forName((String) spriteData.get(0));
                        Sprite sprite = spriteClass.newInstance();
                        sprite.updateState((Map<String, Object>) spriteData.get(1));
                        sprites.put(spriteId, sprite);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (packet.get("action").equals("remove")) {
                    int spriteId = (Integer) packet.get("id");
                    sprites.remove(spriteId);
                } else if (packet.get("action").equals("update")) {
                    int spriteId = (Integer) packet.get("id");
                    if (spriteId != playerId) {
                        sprites.get(spriteId).updateState((Map<String, Object>) packet.get("data"));
                    }
                }
            } else if (packet.get("type").equals("chat")) {
                long timestamp = (Long) packet.get("timestamp");
                String content = (String) packet.get("content");
                chats.add(new Pair<Date, String>(new Date(timestamp), content));
            }
        }
        
        Vector2D acceleration = new Vector2D();
        
        if (!chatting) {
            int xInput = (keysDown.contains(KeyEvent.VK_A) ? -1 : 0) + (keysDown.contains(KeyEvent.VK_D) ? 1 : 0);
            int yInput = (keysDown.contains(KeyEvent.VK_W) ? -1 : 0) + (keysDown.contains(KeyEvent.VK_S) ? 1 : 0);
            
            if (xInput == 0 || yInput == 0) {
                acceleration.x = INPUT_ACCELERATION * xInput;
                acceleration.y = INPUT_ACCELERATION * yInput;
            } else {
                acceleration.x = INPUT_ACCELERATION * xInput / Math.sqrt(2);
                acceleration.y = INPUT_ACCELERATION * yInput / Math.sqrt(2);
            }
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
        
        double xOld = player.getX();
        double yOld = player.getY();
        player.setX(xNew);
        player.setY(yNew);
        
        if (playerId != 0 && xOld != xNew || yOld != yNew) {
            client.sendData(ImmutableMap.<String, Object>of(
                "type", "sprite",
                "action", "update",
                "id", playerId,
                "data", player.serializeState()
            ));
        }
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
        
        // render player separately
        g.translate(player.getX(), player.getY());
        player.render(g);
        g.translate(-player.getX(), -player.getY());
        
        // untranslate camera
        g.translate(-((getWidth() - player.getWidth()) / 2.0 - player.getX()), -((getHeight() - player.getHeight()) / 2.0 - player.getY()));
        
        // draw chat
        g.setFont(FONT_PRIMARY.deriveFont(16f));
        if (chatting) {
            g.setColor(new Color(0x88000000, true));
            g.fillRect(10, getHeight() - 45, getWidth() - 20, 35);
            if (!chats.isEmpty())
                g.fillRect(10, getHeight() - (chats.size() * 25 + 65), getWidth() - 20, chats.size() * 25 + 10);
            
            g.setColor(Color.WHITE);
            g.drawString(currentChat.toString() + "_", 20, getHeight() - 20);
            int i = 1;
            for (Pair<Date, String> chat : chats.descendingSet()) {
                g.drawString(chat.getValue1(), 20, getHeight() - (i * 25 + 40));
                i++;
            }
        } else {
            int i = 1;
            if (!chats.isEmpty()) {
                double firstOpacity = 1.0 - (System.currentTimeMillis() - chats.last().getValue0().getTime() - 5000.0) / 1000.0;
                if (firstOpacity < 0) firstOpacity = 0;
                else if (firstOpacity > 1) firstOpacity = 1;
                g.setColor(new Color(0f, 0f, 0f, (float) (0.345 * firstOpacity)));
                g.fillRect(10, getHeight() - 65, getWidth() - 20, 10);
            }
            for (Pair<Date, String> chat : chats.descendingSet()) {
                double opacity = 1.0 - (System.currentTimeMillis() - chat.getValue0().getTime() - 5000.0) / 1000.0;
                if (opacity < 0) break;
                if (opacity > 1) opacity = 1;
                g.setColor(new Color(0f, 0f, 0f, (float) (0.345 * opacity)));
                g.fillRect(10, getHeight() - (i * 25 + 65), getWidth() - 20, 25);
                g.setColor(new Color(1f, 1f, 1f, (float) opacity));
                g.drawString(chat.getValue1(), 20, getHeight() - (i * 25 + 40));
                i++;
            }
        }
        
    }
    
    public boolean dispatchKeyEvent(KeyEvent ke) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(this, ke);
        return true;
    }
    
    public void keyTyped(KeyEvent ke) {
        if (chatting) {
            if (ke.getKeyChar() != KeyEvent.CHAR_UNDEFINED && Character.getType(ke.getKeyChar()) != Character.CONTROL) {
                currentChat.append(ke.getKeyChar());
            } else {
                if (ke.getKeyChar() == '\u0008') {
                    if (currentChat.length() != 0)
                        currentChat.deleteCharAt(currentChat.length() - 1);
                }
            }
        }
    }
    
    public void keyPressed(KeyEvent ke) {
        if (keysDown.contains(ke.getKeyCode())) return;
        keysDown.add(ke.getKeyCode());
        
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!chatting) {
                chatting = true;
            } else {
                String message = currentChat.toString().trim();
                if (message.length() != 0) {
                    client.sendData(ImmutableMap.<String, Object>of(
                        "type", "chat",
                        "message", currentChat.toString()
                    ));
                }
                currentChat = new StringBuilder();
                chatting = false;
            }
        } else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (chatting) {
                currentChat = new StringBuilder();
                chatting = false;
            }
        }
    }
    
    public void keyReleased(KeyEvent ke) {
        if (!keysDown.contains(ke.getKeyCode())) return;
        keysDown.remove(ke.getKeyCode());
    }
    
}
