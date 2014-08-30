package com.assemblr.arena06.client.scene;

import com.assemblr.arena06.common.data.Sprite;
import com.assemblr.arena06.common.data.map.TileType;
import com.assemblr.arena06.common.data.map.generators.MapGenerator;
import com.assemblr.arena06.common.data.map.generators.RoomGenerator;
import com.assemblr.arena06.common.data.Player;
import com.assemblr.arena06.client.net.PacketClient;
import com.assemblr.arena06.client.utils.DeltaRunnable;
import com.assemblr.arena06.client.utils.DeltaRunner;
import com.assemblr.arena06.common.data.Bullet;
import com.assemblr.arena06.common.data.UpdateableSprite;
import com.assemblr.arena06.common.resource.ResourceBlock;
import com.assemblr.arena06.common.resource.ResourceResolver;
import com.assemblr.arena06.common.utils.Fonts;
import com.assemblr.arena06.common.utils.Vector2D;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.javatuples.Pair;

public class GameScene extends Scene implements KeyEventDispatcher, KeyListener, MouseListener, MouseWheelListener {

    private static final double INPUT_ACCELERATION = 4000;
    private static final double FRICTION_ACCELERATION = 2000;
    private static final double MAXIMUM_VELOCITY = 400;

    private final Random random = new Random();

    private final PacketClient client;

    private DeltaRunner runner;

    private Thread shutdownHook;

    private int playerId = 0;
    private Player player;
    private Map<Integer, Sprite> sprites = new HashMap<Integer, Sprite>();
    private Map<Integer, UpdateableSprite> updateableSprites = new ConcurrentHashMap<Integer, UpdateableSprite>();
    private MapGenerator mapGenerator = new RoomGenerator();
    private TileType[][] map;
    private BufferedImage mapBuffer;

    private boolean chatting = false;
    private StringBuilder currentChat = new StringBuilder();
    private TreeSet<Pair<Date, String>> chats = new TreeSet<Pair<Date, String>>();

    private Vector2D velocity = new Vector2D();

    private Set<Integer> keysDown = new HashSet<Integer>();
    private boolean mouseDown;

    public GameScene(String ipAddress, int port, String username) {
        InetSocketAddress serverAddress = new InetSocketAddress(ipAddress, port);
        System.out.println("connecting to server at " + serverAddress);
        client = new PacketClient(serverAddress);
        player = new Player(true, username);
        setPreferredSize(new Dimension(800, 600));
        addKeyListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void sceneWillAppear() {

        ResourceResolver.getResourceResolver().loadResources(ResourceBlock.SPRITES);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
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

        runner = new DeltaRunner(60, new Runnable() {
            public void run() {
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
            }
        }, new DeltaRunnable() {
            public void run(double delta) {
                update(delta);
            }
        });
        runner.start();

        shutdownHook = new Thread() {
            @Override
            public void run() {
                client.sendDataBlocking(ImmutableMap.<String, Object>of(
                        "type", "logout"
                ));
            }
        };

        client.sendData(ImmutableMap.<String, Object>of(
                "type", "login",
                "data", player.serializeState()
        ));
        System.out.println("logging in...");

        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    @Override
    public void sceneWillDisappear() {
        runner.requestStop();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
    }

    private void generateMap(long seed) {
        map = mapGenerator.generateMap(seed);
        paintMap();
    }

    private void paintMap() {
        mapBuffer = getGraphicsConfiguration().createCompatibleImage(map.length * (int) MapGenerator.TILE_SIZE, (map.length == 0 ? 0 : map[0].length) * (int) MapGenerator.TILE_SIZE);
        Graphics2D g = mapBuffer.createGraphics();
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == TileType.NONE) {
                    continue;
                }
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
                requestSpriteList();
                System.out.println("logged in as " + player.getName());
            } else if (packet.get("type").equals("request")) {
                if (packet.get("request").equals("sprite-list")) {
                    // refresh sprite list
                    sprites.clear();
                    Map<String, Object> spriteList = (Map<String, Object>) packet.get("data");
                    for (Map.Entry<String, Object> entry : spriteList.entrySet()) {
                        int spriteId = Integer.parseInt(entry.getKey());
                        List<Object> spriteData = (List<Object>) entry.getValue();
                        if (spriteId == playerId) {
                            player = new Player(true, player.getName());
                            player.updateState((Map<String, Object>) spriteData.get(1));
                            client.sendData(
                                    ImmutableMap.<String, Object>of(
                                            "type", "sprite",
                                            "action", "validate",
                                            "id", playerId
                                    )
                            );
                            continue;
                        }

                        try {
                            Class<? extends Sprite> spriteClass = (Class<? extends Sprite>) Class.forName((String) spriteData.get(0));
                            Sprite sprite = spriteClass.newInstance();
                            sprite.updateState((Map<String, Object>) spriteData.get(1));
                            sprites.put(spriteId, sprite);
                            if (sprite instanceof UpdateableSprite) {
                                updateableSprites.put(spriteId, (UpdateableSprite) sprite);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    requestingSpriteList = false;
                }
            } else if (packet.get("type").equals("map")) {
                if (packet.get("action").equals("load")) {
                    generateMap((Long) packet.get("seed"));
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
                        if (sprite instanceof UpdateableSprite) {
                            updateableSprites.put(spriteId, (UpdateableSprite) sprite);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (packet.get("action").equals("remove")) {
                    int spriteId = (Integer) packet.get("id");
                    sprites.remove(spriteId);
                    if (updateableSprites.containsKey(spriteId)) {
                        updateableSprites.remove(spriteId);
                    }
                } else if (packet.get("action").equals("update")) {
                    int spriteId = (Integer) packet.get("id");
                    if (spriteId != playerId) {
                        Sprite s = sprites.get(spriteId);
                        if (s != null) {
                            s.updateState((Map<String, Object>) packet.get("data"));
                        } else {
                            System.out.println("req sprite list because of missing sprite");
                            requestSpriteList();
                        }
                    } else {
                        Map<String, Object> playerState = (Map<String, Object>) packet.get("data");
                        player.updateState(playerState);
                        client.sendData(ImmutableMap.<String, Object>of(
                                "type", "sprite",
                                "action", "validate",
                                "id", playerId
                        ));
                    }
                }
            } else if (packet.get("type").equals("chat")) {
                long timestamp = (Long) packet.get("timestamp");
                String content = (String) packet.get("content");
                chats.add(new Pair<Date, String>(new Date(timestamp), content));
            }
        }

        boolean playerNeedsUpdate = false;
        for (Map.Entry<Integer, UpdateableSprite> updateableSprite : updateableSprites.entrySet()) {
            updateableSprite.getValue().update(delta);
        }

        if (player.cooldownRemaining() != 0) {
            player.setCooldownRemaining(player.cooldownRemaining() - delta);
            if (player.cooldownRemaining() < 0) {
                player.setCooldownRemaining(0);
            }
        }
        if (player.isReloading()) {
            player.setReloadRemaining(player.getReloadRemaining() - delta);
            if (player.getReloadRemaining() < 0) {
                player.setReloadRemaining(0);
                player.setIsReloading(false);
                player.setLoadedBullets(player.getWeapon().getMagSize());
                playerNeedsUpdate = true;
            }
        }
        Point mouseLocationOnScrene = new Point(0, 0);
        try {
            mouseLocationOnScrene = new Point(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
        } catch (Exception e) {}
        double oldDirection = player.getDirection();
        player.setDirection(Vector2D.getAngle(new Vector2D(mouseLocationOnScrene.x - this.getWidth() / 2, mouseLocationOnScrene.y - this.getHeight() / 2)));
        if (player.getDirection() != oldDirection) {
            playerNeedsUpdate = true;
        }
        if (mouseDown && player.isAlive() && !player.isReloading() && player.cooldownRemaining() == 0 && player.getWeapon().isFullAuto() && !player.getWeaponData().isOutOfAmo()) {
            shoot(mouseLocationOnScrene);
        }
        Vector2D oldVelocity = new Vector2D(getVelocity());
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
        getVelocity().add(Vector2D.multiply(acceleration, delta));

        if (Vector2D.length(getVelocity()) > 0) {
            Vector2D friction = Vector2D.scale(getVelocity(), FRICTION_ACCELERATION);
            getVelocity().subtract(Vector2D.multiply(friction, delta).clamp(Vector2D.length(getVelocity())));
        }
        getVelocity().clamp(MAXIMUM_VELOCITY);

        if (Math.abs(getVelocity().x) < 0.001) {
            getVelocity().x = 0;
        }
        if (Math.abs(getVelocity().y) < 0.001) {
            getVelocity().y = 0;
        }

        // perform collision detection
        double xNew = player.getX() + getVelocity().x * delta;
        double yNew = player.getY() + getVelocity().y * delta;
        if (map != null) {
            if (xNew < 0) {
                xNew = 0;
                getVelocity().x = 0;
            }
            if (xNew > MapGenerator.TILE_SIZE * map.length) {
                xNew = MapGenerator.TILE_SIZE * map.length;
                getVelocity().x = 0;
            }
            if (yNew < 0) {
                yNew = 0;
                getVelocity().y = 0;
            }
            if (yNew > MapGenerator.TILE_SIZE * map[0].length) {
                yNew = MapGenerator.TILE_SIZE * map.length;
                getVelocity().y = 0;
            }

            if (player.isAlive()) {
                if (getVelocity().x < 0) {
                    int xTile = (int) (xNew / MapGenerator.TILE_SIZE);
                    for (int yTile = player.getTileY(); yTile <= (int) ((player.getY() + player.getHeight()) / MapGenerator.TILE_SIZE); yTile++) {
                        if (map[xTile][yTile].isSolid()) {
                            xNew = (xTile + 1) * MapGenerator.TILE_SIZE;
                            getVelocity().x = 0;
                        }
                    }
                } else if (getVelocity().x > 0) {
                    int xTile = (int) ((xNew + player.getWidth()) / MapGenerator.TILE_SIZE);
                    for (int yTile = player.getTileY(); yTile <= (int) ((player.getY() + player.getHeight()) / MapGenerator.TILE_SIZE); yTile++) {
                        if (map[xTile][yTile].isSolid()) {
                            xNew = xTile * MapGenerator.TILE_SIZE - player.getWidth() - 0.01;
                            getVelocity().x = 0;
                        }
                    }
                }

                if (getVelocity().y < 0) {
                    int yTile = (int) (yNew / MapGenerator.TILE_SIZE);
                    for (int xTile = player.getTileX(); xTile <= (int) ((player.getX() + player.getWidth()) / MapGenerator.TILE_SIZE); xTile++) {
                        if (map[xTile][yTile].isSolid()) {
                            yNew = (yTile + 1) * MapGenerator.TILE_SIZE;
                            getVelocity().y = 0;
                        }
                    }
                } else if (getVelocity().y > 0) {
                    int yTile = (int) ((yNew + player.getHeight()) / MapGenerator.TILE_SIZE);
                    for (int xTile = player.getTileX(); xTile <= (int) ((player.getX() + player.getWidth()) / MapGenerator.TILE_SIZE); xTile++) {
                        if (map[xTile][yTile].isSolid()) {
                            yNew = yTile * MapGenerator.TILE_SIZE - player.getHeight() - 0.01;
                            getVelocity().y = 0;
                        }
                    }
                }
            }
        }
        double xOld = player.getX();
        double yOld = player.getY();
        player.setX(xNew);
        player.setY(yNew);
        player.setVelocity(getVelocity());
        if (playerId != 0 && 
                (xOld != xNew ||
                yOld != yNew || 
                playerNeedsUpdate
                )
                ) {
                client.sendData(ImmutableMap.<String, Object>of(
                        "type", "sprite",
                        "action", "update",
                        "id", playerId,
                        "data", player.serializeState()
                ));
        }
    }

    private boolean requestingSpriteList = false;

    private synchronized void requestSpriteList() {
        System.out.println("requesting a sprite list.");
        if (requestingSpriteList) {
            return;
        }
        requestingSpriteList = true;
        client.sendData(ImmutableMap.<String, Object>of(
                "type", "request",
                "request", "sprite-list"
        ));
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

        // draw weapon info
        if (player.isAlive()) {
            g.setColor(Color.red);
            g.setFont(Fonts.FONT_PRIMARY.deriveFont(16f));
            g.drawString(player.getWeapon().getName(), this.getWidth() - 120 - g.getFontMetrics().stringWidth(player.getWeapon().getName()), this.getHeight() - 10);
            g.setColor(Color.white);
            g.drawRect(this.getWidth() - 109, this.getHeight() - 19, 104, 14);
            if (player.isReloading()) {
                g.setColor(Color.red);
                g.fillRect(this.getWidth() - 107, this.getHeight() - 17, (int) Math.round(101 * ((double) player.getWeapon().getReloadTime() - (double) player.getReloadRemaining()) / (double) player.getWeapon().getReloadTime()), 11);
            } else {
                g.fillRect(this.getWidth() - 107, this.getHeight() - 17, (int) Math.round(101 * (double) player.getLoadedBullets() / (double) player.getWeapon().getMagSize()), 11);
            }

            g.drawString("" + player.getWeaponData().getCartregesReamaining(), this.getWidth() - 150 - g.getFontMetrics().stringWidth(player.getWeapon().getName()), this.getHeight() - 10);
        }

        // draw chat
        g.setFont(Fonts.FONT_PRIMARY.deriveFont(16f));
        if (chatting) {
            g.setColor(new Color(0x88000000, true));
            g.fillRect(10, getHeight() - 45, getWidth() - 20, 35);
            g.setColor(Color.WHITE);
            g.drawString(currentChat.toString() + "_", 20, getHeight() - 20);

            int i = 1;
            if (!chats.isEmpty()) {
                g.setColor(new Color(0x88000000, true));
                g.fillRect(10, getHeight() - 65, getWidth() - 20, 10);
            }
            for (Pair<Date, String> chat : chats.descendingSet()) {
                for (String line : Lists.reverse(Arrays.asList(chat.getValue1().trim().split("\n")))) {
                    g.setColor(new Color(0x88000000, true));
                    g.fillRect(10, getHeight() - (i * 25 + 65), getWidth() - 20, 25);
                    g.setColor(Color.WHITE);
                    g.drawString(line, 20, getHeight() - (i * 25 + 40));
                    i++;
                }
            }
        } else {
            // drawing is separated into two parts to avoid opacity overlap
            int i = 1;
            // draw bottom line for first line of chat
            if (!chats.isEmpty()) {
                double firstOpacity = 1.0 - (System.currentTimeMillis() - chats.last().getValue0().getTime() - 5000.0) / 1000.0;
                if (firstOpacity < 0) {
                    firstOpacity = 0;
                } else if (firstOpacity > 1) {
                    firstOpacity = 1;
                }
                g.setColor(new Color(0f, 0f, 0f, (float) (0.345 * firstOpacity)));
                g.fillRect(10, getHeight() - 65, getWidth() - 20, 10);
            }
            // draw top part of remaining lines
            for (Pair<Date, String> chat : chats.descendingSet()) {
                double opacity = 1.0 - (System.currentTimeMillis() - chat.getValue0().getTime() - 5000.0) / 1000.0;
                if (opacity < 0) {
                    break;
                }
                if (opacity > 1) {
                    opacity = 1;
                }
                for (String line : Lists.reverse(Arrays.asList(chat.getValue1().trim().split("\n")))) {
                    g.setColor(new Color(0f, 0f, 0f, (float) (0.345 * opacity)));
                    g.fillRect(10, getHeight() - (i * 25 + 65), getWidth() - 20, 25);
                    g.setColor(new Color(1f, 1f, 1f, (float) opacity));
                    g.drawString(line, 20, getHeight() - (i * 25 + 40));
                    i++;
                }
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
                    if (currentChat.length() != 0) {
                        currentChat.deleteCharAt(currentChat.length() - 1);
                    }
                }
            }
        }
    }

    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.out.println("disconnection from server");
            getNavigationController().popScene();
        }
        if (ke.getKeyChar() == 'e' || ke.getKeyChar() == 'E') {
            player.incrementWeaponIndex(1);
        } else if (ke.getKeyChar() == 'q' || ke.getKeyChar() == 'Q') {
            player.incrementWeaponIndex(-1);
        }
        if (keysDown.contains(ke.getKeyCode())) {
            return;
        }
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
        if (!keysDown.contains(ke.getKeyCode())) {
            return;
        }
        keysDown.remove(ke.getKeyCode());
    }

    @Override
    public void dispose() {
        client.sendDataBlocking(ImmutableMap.<String, Object>of(
                "type", "logout"
        ));
        ResourceResolver.getResourceResolver().unloadResources(ResourceBlock.SPRITES);
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
    }

    private void shoot(Point direction) {
        List<Bullet> bullets = player.getWeapon().getBulletFactory().getBullets();
        for (Bullet b : bullets) {
            b.setOwner(player.getName());
            double angle = player.getDirection();
            b.setVelocity(b.getVelocity().addAngle(angle));
            b.setPosition((new Vector2D(angle, player.getWidth() * Math.sqrt(2) / 2, true)).add(player.getCenter()).getPoint());
            client.sendDataBlocking(ImmutableMap.<String, Object>of(
                    "type", "sprite",
                    "action", "create",
                    "data", ImmutableList.<Object>of(Bullet.class.getName(), b.serializeState())
            ));
        }
        player.setCooldownRemaining(player.getWeapon().getFireTime());
        player.setLoadedBullets(player.getLoadedBullets() - 1);
        if (player.getLoadedBullets() <= 0) {
            player.getWeaponDataModifyable().reload();
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        if (player.isAlive() && player.cooldownRemaining() == 0 && !player.isReloading() && !player.getWeaponData().isOutOfAmo()) {
            shoot(e.getPoint());
        }
    }

    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        player.incrementWeaponIndex(e.getClickCount());
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

}
