package com.assemblr.oneshot.data.map.generators;

import com.assemblr.oneshot.data.map.TileType;
import com.assemblr.oneshot.utils.Direction;
import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;


public final class RoomGenerator implements MapGenerator {
    
    // DEBUG
    public static void main(String[] args) {
        RoomGenerator generator = new RoomGenerator();
        TileType[][] map = generator.generateMap();
        for (TileType[] row : map) {
            for (TileType tile : row) {
                switch (tile) {
                case NONE:
                    System.out.print(" ");
                    break;
                case FLOOR:
                    System.out.print(".");
                    break;
                case WALL:
                    System.out.print("X");
                    break;
                case DOOR:
                    System.out.print(",");
                    break;
                }
            }
            System.out.println();
        }
    }
    
    private static final class Room {
        private int x;
        private int y;
        private int width;
        private int height;
        private Set<Door> doors = new HashSet<Door>();
        
        public Room(int x, int y, int width, int height) {
            this(x, y, width, height, null);
        }
        
        public Room(int x, int y, int width, int height, Set<Point> doors) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            if (doors != null) {
                addDoors(doors);
            }
        }
        
        public int getWidth() {
            return width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public Set<Door> getDoors() {
            return doors;
        }
        
        public void addDoors(Set<Point> doors) {
            for (Point door : doors) addDoor(door);
        }
        
        public void addDoor(Point door) {
            if (door.x != 0 && door.x != getWidth()  - 1 &&
                door.y != 0 && door.y != getHeight() - 1) {
                throw new RuntimeException("Non-edge door location added to room");
            }
            if ((door.x == 0              && door.y == 0              ) ||
                (door.x == 0              && door.y == getHeight() - 1) ||
                (door.x == getWidth() - 1 && door.y == 0              ) ||
                (door.x == getWidth() - 1 && door.y == getHeight() - 1)) {
                throw new RuntimeException("Corner door location added to room");
            }
            
            Direction direction;
            if (door.x == 0) {
                direction = Direction.WEST;
            } else if (door.x == getWidth() - 1) {
                direction = Direction.EAST;
            } else if (door.y == 0) {
                direction = Direction.NORTH;
            } else {
                direction = Direction.SOUTH;
            }
            
            Point worldLocation = new Point(x + door.x, y + door.y);
            doors.add(new Door(worldLocation, direction));
        }
        
        public int getX() {
            return x;
        }
        
        public void setX(int x) {
            this.x = x;
        }
        
        public int getY() {
            return y;
        }
        
        public void setY(int y) {
            this.y = y;
        }
    }
    
    private static final class Door {
        private final Point location;
        private final Direction direction;
        
        public Door(Point location, Direction direction) {
            this.location = location;
            this.direction = direction;
        }
        
        public Point getLocation() {
            return location;
        }
        
        public Direction getDirection() {
            return direction;
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.location != null ? this.location.hashCode() : 0);
            hash = 37 * hash + (this.direction != null ? this.direction.hashCode() : 0);
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            final Door other = (Door) obj;
            if (this.location != other.location && (this.location == null || !this.location.equals(other.location))) return false;
            if (this.direction != other.direction) return false;
            return true;
        }
    }
    
    private Random generator;
    private double generationProbability;
    private List<Room> roomList;
    private Queue<Door> openDoors;
    
    public TileType[][] generateMap() {
        generator = new Random();
        generationProbability = 1.0;
        roomList = new LinkedList<Room>();
        openDoors = new LinkedList<Door>();
        
        Room mainRoom = generateRoom();
        mainRoom.addDoors(generateDoors(mainRoom, null));
        openDoors.addAll(mainRoom.getDoors());
        roomList.add(mainRoom);
        
        while (!openDoors.isEmpty()) {
            generationProbability *= 0.5;
            Door door = openDoors.poll();
            Room room = generateRoom();
            
            // place room on map
            switch (door.getDirection()) {
            case NORTH:
                room.x = door.getLocation().x - room.getWidth()/2 /*+ generator.nextInt(room.getWidth() - 2) + 1*/;
                room.y = door.getLocation().y - room.getHeight() + 1;
                break;
            case SOUTH:
                room.x = door.getLocation().x - room.getWidth()/2 /*+ generator.nextInt(room.getWidth() - 2) + 1*/;
                room.y = door.getLocation().y;
                break;
            case WEST:
                room.x = door.getLocation().x - room.getWidth() + 1;
                room.y = door.getLocation().y - room.getHeight()/2 /*+ generator.nextInt(room.getHeight() - 2) + 1*/;
                break;
            case EAST:
                room.x = door.getLocation().x;
                room.y = door.getLocation().y - room.getHeight()/2 /*+ generator.nextInt(room.getHeight() - 2) + 1*/;
                break;
            }
            
            room.addDoors(generateDoors(room, door.getDirection().getOpposite()));
            openDoors.addAll(room.getDoors());
            roomList.add(room);
        }
        
        return generateMap(roomList);
    }
    
    private Room generateRoom() {
        return new Room(0, 0, generator.nextInt(10) + 5, generator.nextInt(10) + 5);
    }
    
    private Set<Point> generateDoors(Room room, Direction existingDirection) {
        Set<Point> doors = new HashSet<Point>();
        
        for (Direction direction : Direction.values()) {
            if (generator.nextDouble() <= generationProbability) {
                doors.add(generateDoor(room, direction));
            }
        }
        
        return doors;
    }
    
    private Point generateDoor(Room room, Direction direction) {
        switch (direction) {
        case NORTH:
            return new Point(generator.nextInt(room.getWidth() - 2) + 1, 0);
        case SOUTH:
            return new Point(generator.nextInt(room.getWidth() - 2) + 1, room.getHeight() - 1);
        case EAST:
            return new Point(0, generator.nextInt(room.getHeight() - 2) + 1);
        case WEST:
            return new Point(room.getWidth() - 1, generator.nextInt(room.getHeight() - 2) + 1);
        default:
            return null;
        }
    }
    
    private static void setEmptyTile(TileType[][] data, int x, int y, TileType type) {
        if (data[x][y] == TileType.NONE)
            data[x][y] = type;
    }
    
    private TileType[][] generateMap(List<Room> rooms) {
        int xMin = Integer.MAX_VALUE, xMax = Integer.MIN_VALUE, yMin = Integer.MAX_VALUE, yMax = Integer.MIN_VALUE;
        
        // calculate total map dimensions
        for (Room room : rooms) {
            if (room.getX() < xMin) xMin = room.getX();
            else if (room.getX() + room.getWidth() - 1 > xMax) xMax = room.getX() + room.getWidth() - 1;
            if (room.getY() < yMin) yMin = room.getY();
            else if (room.getY() + room.getHeight() - 1 > yMax) yMax = room.getY() + room.getHeight() - 1;
        }
        
        int width = xMax - xMin + 1;
        int height = yMax - yMin + 1;
        TileType[][] data = new TileType[width][height];
        
        // initialize map
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                data[x][y] = TileType.NONE;
            }
        }
        
        // assemble map
        for (Room room : rooms) {
            // normalize coordinates
            int xNorm = room.getX() - xMin;
            int yNorm = room.getY() - yMin;
            
            // apply room walls
            for (int x = 0; x < room.getWidth(); x++) {
                setEmptyTile(data, xNorm + x, yNorm, TileType.WALL);
                setEmptyTile(data, xNorm + x, yNorm + room.getHeight() - 1, TileType.WALL);
            }
            for (int y = 0; y < room.getHeight(); y++) {
                setEmptyTile(data, xNorm, yNorm + y, TileType.WALL);
                setEmptyTile(data, xNorm + room.getWidth() - 1, yNorm + y, TileType.WALL);
            }
            
            // apply room floor
            for (int x = 1; x < room.getWidth() - 1; x++) {
                for (int y = 1; y < room.getHeight() - 1; y++) {
                    setEmptyTile(data, xNorm + x, yNorm + y, TileType.FLOOR);
                }
            }
            
            // apply room doors
            for (Door door : room.getDoors()) {
                data[door.getLocation().x - xMin][door.getLocation().y - yMin] = TileType.DOOR;
            }
        }
        
        return data;
    }
    
}
